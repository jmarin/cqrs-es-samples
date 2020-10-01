package com.github.jmarin.cqrs.lagom.bank

import com.lightbend.lagom.scaladsl.api.ServiceCall
import akka.NotUsed
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import scala.concurrent.ExecutionContext
import com.lightbend.lagom.scaladsl.api.transport.BadRequest
import com.lightbend.lagom.scaladsl.persistence.ReadSide
import com.github.jmarin.cqrs.lagom.bank.readside.{
  AccountRepository,
  ReadSideAccount
}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.github.jmari.cqrs.lagom.bank.FeeTransfered
import com.github.jmari.cqrs.lagom.bank.AccountTopicEvent
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.EventStreamElement
import kafka.utils.immutable
import com.github.jmari.cqrs.lagom.bank.UnknownEvent

class AccountServiceImpl(
    persistentEntityRegistry: PersistentEntityRegistry,
    repository: AccountRepository
)(
    implicit ec: ExecutionContext
) extends AccountService {

  override def accountTopic(): Topic[AccountTopicEvent] =
    TopicProducer.taggedStreamWithOffset(AccountEvent.Tag.allTags.toList) {
      (tag, fromOffset) =>
        persistentEntityRegistry
          .eventStream(tag, fromOffset)
          .mapConcat(filterEvents)
    }

  private def filterEvents(
      eventElement: EventStreamElement[AccountEvent]
  ) = eventElement match {
    case ev @ EventStreamElement(id, MoneyTransferred(to, amount), offset) =>
      scala.collection.immutable.Seq((convertEvent(ev), offset))
    case _ => Nil
  }

  private def convertEvent(
      accountStream: EventStreamElement[AccountEvent]
  ): AccountTopicEvent =
    accountStream.event match {
      case MoneyTransferred(to, amount) => FeeTransfered(to, amount)
      case _                            => UnknownEvent("")
    }

  private def entityRef(id: String) =
    persistentEntityRegistry.refFor[AccountEntity](id)

  def createAccount: ServiceCall[CreateAccount, Account] = { createAccount =>
    val account = Account(createAccount.accountId, createAccount.initialBalance)
    val ref = entityRef(createAccount.accountId)
    ref.ask(OpenAccount(account)).map(_ => account)
  }

  def deposit(id: String): ServiceCall[DepositMoney, Account] = {
    depositMoney =>
      val ref = entityRef(id)
      for {
        _ <- ref.ask(Deposit(depositMoney.amount))
        state <- ref.ask(Get)
      } yield Account(id, state.balance)
  }

  def withdraw(id: String): ServiceCall[WithdrawMoney, Account] = {
    withdrawMoney =>
      val ref = entityRef(id)
      val f = for {
        _ <- ref.ask(Withdraw(withdrawMoney.amount))
        state <- ref.ask(Get)
      } yield Account(id, state.balance)

      f.recover {
        case e: AccountException => throw BadRequest(e.message)
      }
  }

  def transfer(id: String): ServiceCall[TransferToAccount, Account] = {
    transferToAccount =>
      val ref = entityRef(id)
      val f = for {
        _ <- ref.ask(
          TransferMoney(transferToAccount.to, transferToAccount.amount)
        )
        state <- ref.ask(Get)
      } yield Account(id, state.balance)

      f.recover {
        case e: AccountException => throw BadRequest(e.message)
      }

  }

  def get(id: String): ServiceCall[NotUsed, Account] = { _ =>
    val ref = entityRef(id)
    ref.ask(Get).map(state => Account(id, state.balance))
  }

  def getAll(): ServiceCall[NotUsed, Seq[Account]] = { _ =>
    repository.getAll().map(_.map(toApi))
  }

  private def toApi: ReadSideAccount => Account = { readSideAccount =>
    Account(readSideAccount.id, readSideAccount.balance)
  }

}
