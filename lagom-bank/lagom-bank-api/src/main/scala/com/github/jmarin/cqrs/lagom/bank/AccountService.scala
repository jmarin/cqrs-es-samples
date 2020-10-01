package com.github.jmarin.cqrs.lagom.bank

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.Service
import com.lightbend.lagom.scaladsl.api.Descriptor
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.github.jmari.cqrs.lagom.bank.FeeTransfered

trait AccountService extends Service {

  def createAccount: ServiceCall[CreateAccount, Account]
  def deposit(id: String): ServiceCall[DepositMoney, Account]
  def withdraw(id: String): ServiceCall[WithdrawMoney, Account]
  def get(id: String): ServiceCall[NotUsed, Account]
  def transfer(id: String): ServiceCall[TransferToAccount, Account]
  def getAll(): ServiceCall[NotUsed, Seq[Account]]

  def moneyTransferTopic(): Topic[FeeTransfered]

  override def descriptor: Descriptor = {
    import Service._
    named("accounts")
      .withCalls(
        restCall(Method.POST, "/accounts", createAccount),
        restCall(Method.GET, "/accounts", getAll),
        restCall(Method.PUT, "/accounts/:id/deposit", deposit _),
        restCall(Method.PUT, "/accounts/:id/withdraw", withdraw _),
        restCall(Method.PUT, "/accounts/:id/transfer", transfer _),
        restCall(Method.GET, "/accounts/:id", get _)
      )
      .withTopics(
        topic(AccountService.MONEY_TRANSFER_TOPIC_NAME, moneyTransferTopic)
      )
      .withAutoAcl(true)
  }

  object AccountService {
    val MONEY_TRANSFER_TOPIC_NAME = "accounts"
  }
}
