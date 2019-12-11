package com.github.jmarin.cqrs.lagom.bank

import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import scala.concurrent.ExecutionContext
import com.lightbend.lagom.scaladsl.api.ServiceCall
import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.BadRequest

class AccountServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends AccountService {

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

}