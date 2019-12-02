package com.github.jmarin.cqrs.lagom.bank

import org.scalatest.WordSpec
import org.scalatest.Matchers

class AccountStateSpec extends WordSpec with Matchers {

  val state = EmptyAccountState()
  val account = Account("account-123", 0)

  "Account State" should {
    "Open Account, deposit, withdraw and transfer money" in {
      val opened = state.applyEvent(AccountOpened(account))
      opened shouldBe AccountState(true, account.balance)

      val deposited = opened.applyEvent(Deposited(150))
      deposited shouldBe AccountState(true, 150)

      val withdrawn = deposited.applyEvent(Withdrawn(100))
      withdrawn shouldBe AccountState(true, 50)

      val transferred = withdrawn
        .applyEvent(MoneyTransferred(account.id, 25))
        .applyEvent(TransferFeeDeducted(0.25))
      transferred shouldBe AccountState(true, 24.75)

    }
  }
}
