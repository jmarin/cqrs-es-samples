package com.github.jmarin.cqrs.lagom.bank

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.PropertyChecks
import AccountGenerator._
import play.api.libs.json.Json

class AccountStateSpec extends PropSpec with PropertyChecks with Matchers {

  val state = EmptyAccountState()
  val account = Account("account-123", 0)

  property("AccountState should serialize/deserialize to/from JSON") {
    forAll { as: AccountState =>
      val json = Json.toJson(as)
      Json.parse(json.toString).as[AccountState] shouldBe as
    }
  }

  property("Account should open account, deposit, withdraw and transfer money") {
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
