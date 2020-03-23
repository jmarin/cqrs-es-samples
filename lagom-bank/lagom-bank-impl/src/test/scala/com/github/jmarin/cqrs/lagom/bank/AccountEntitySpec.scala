package com.github.jmarin.cqrs.lagom.bank

import org.scalatest.{WordSpec, Matchers, BeforeAndAfterAll}
import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry

class AccountEntitySpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private val system = ActorSystem(
    "AccountEntitySpec",
    JsonSerializerRegistry.actorSystemSetupFor(AccountSerializerRegistry)
  )

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  private val account = Account("account-1", 100)
  private val receiveAccount = Account("account-2", 20)

  private def withTestDriver(
      block: PersistentEntityTestDriver[AccountCommand[_], AccountEvent, AccountState] => Unit
  ): Unit = {
    val driver =
      new PersistentEntityTestDriver(system, new AccountEntity, account.id)
    block(driver)
    driver.getAllIssues should have size 0
  }

  "Account Entity" should withTestDriver { driver =>
    "Open an account" in {
      val outcome = driver.run(OpenAccount(account))
      outcome.replies should have size 1
      outcome.replies should contain only OpenAccountDone(account)
    }

    "Deposit money" in {
      val deposit = driver.run(Deposit(250))
      deposit.replies should have size 1
      deposit.replies should contain only DepositDone(250)

      val balance = driver.run(Get)
      balance.replies should have size 1
      balance.replies should contain only AccountState(true, 350)
    }

    "Fail withdraw when not enough funds" in {
      val withdraw = driver.run(Withdraw(500))
      withdraw.replies.head shouldBe a[AccountException]
      withdraw.events should have size 0
    }

    "Withdraw Money" in {
      val withdraw = driver.run(Withdraw(150))
      withdraw.replies should have size 1
      withdraw.replies should contain only WithdrawDone(150)

      val balance = driver.run(Get)
      balance.replies should have size 1
      balance.replies should contain only AccountState(true, 200)
    }

    "Fail transfer below minimun required threshold" in {
      val transfer = driver
        .run(TransferMoney(receiveAccount.id, 4))
      transfer.replies.head shouldBe a[AccountException]
      transfer.events should have size 0
    }

    "Fail transfer money when infufficient funds" in {
      val transfer =
        driver.run(TransferMoney(receiveAccount.id, 199.9))
      transfer.replies.head shouldBe a[AccountException]
      transfer.events should have size 0
    }

    "Transfer Money to another account" in {
      val transfer = driver.run(TransferMoney(receiveAccount.id, 195))
      transfer.replies should have size 1
      transfer.replies should contain only TransferMoneyDone(
        receiveAccount.id,
        195
      )

      val balance = driver.run(Get)
      balance.replies should have size 1
      balance.replies should contain only AccountState(true, 4.75)
    }
  }

}
