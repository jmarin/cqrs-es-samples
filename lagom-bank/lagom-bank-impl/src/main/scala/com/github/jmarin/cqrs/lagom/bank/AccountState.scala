package com.github.jmarin.cqrs.lagom.bank

import play.api.libs.json.{Format, Json}

case class AccountState(open: Boolean = false, balance: BigDecimal) {
  def applyEvent(event: AccountEvent): AccountState = event match {
    case AccountOpened(account) =>
      AccountState(true, account.balance)
    case Deposited(amount) =>
      val newBalance = balance + amount
      this.copy(balance = newBalance)
    case Withdrawn(amount) =>
      val newBalance = balance - amount
      this.copy(balance = newBalance)
    case MoneyTransferred(_, amount) =>
      val newBalance = balance - amount
      println(s"balance: $balance, new balance: $newBalance")
      this.copy(balance = newBalance)
    case TransferFeeDeducted(amount) =>
      val newBalance = balance - amount
      this.copy(balance = newBalance)
    case _ => this
  }
}

object AccountState {
  implicit val format: Format[AccountState] = Json.format
}

object EmptyAccountState {
  def apply(): AccountState = AccountState(false, 0)
}
