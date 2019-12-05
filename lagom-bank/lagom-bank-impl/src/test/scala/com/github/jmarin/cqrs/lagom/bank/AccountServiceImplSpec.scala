package com.github.jmarin.cqrs.lagom.bank

import org.scalatest.AsyncWordSpec
import org.scalatest.Matchers
import org.scalatest.BeforeAndAfterAll
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
import com.lightbend.lagom.scaladsl.api.transport.BadRequest

class AccountServiceImplSpec
    extends AsyncWordSpec
    with Matchers
    with BeforeAndAfterAll {

  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup.withCassandra()
  ) { ctx =>
    new AccountApplication(ctx) with LocalServiceLocator
  }

  val client = server.serviceClient.implement[AccountService]

  override protected def afterAll(): Unit = server.stop()

  val accountId = "account-123"
  val receivingId = "account-456"

  "Account Service" should {
    "Create an account" in {
      client.createAccount.invoke(CreateAccount(accountId, 100)).map {
        account =>
          account.balance shouldBe 100
          account.id shouldBe accountId
      }

      client.createAccount.invoke(CreateAccount(receivingId, 0)).map {
        account =>
          account.balance shouldBe 0
          account.id shouldBe receivingId
      }
    }

    "Deposit money" in {
      client
        .deposit(accountId)
        .invoke(DepositMoney(200))
        .map(account => account.balance shouldBe 300)
    }

    "Withdraw money" in {
      client
        .withdraw(accountId)
        .invoke(WithdrawMoney(150))
        .map(account => account.balance shouldBe 150)
    }

    "Fail to withdraw when insufficient funds" in {
      val f = client
        .withdraw(accountId)
        .invoke(WithdrawMoney(1000))

      recoverToSucceededIf[BadRequest](f)
    }

    "Transfer money" in {
      client
        .transfer(accountId)
        .invoke(TransferToAccount(receivingId, 50))
        .map(account => account.balance shouldBe 99.75)
    }

    "Fail to transfer when insufficient funds" in {
      val f = client
        .transfer(accountId)
        .invoke(TransferToAccount(receivingId, 99.60))

      recoverToSucceededIf[BadRequest](f)
    }

    "retrieve all accounts" in {
      client.getAll().invoke().map { xs =>
        xs.size shouldBe 2
      }
    }

  }

}
