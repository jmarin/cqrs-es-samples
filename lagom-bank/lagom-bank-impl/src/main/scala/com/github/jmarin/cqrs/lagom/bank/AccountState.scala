package com.github.jmarin.cqrs.lagom.bank

case class AccountState(open: Boolean = false, balance: BigDecimal) {

  def empty: AccountState = AccountState(balance = 0)

  def applyEvent(event: AccountEvent): AccountState = event match {
    case AccountOpened(account) =>
      AccountState(true, account.balance)
    case Deposited(amount) =>
      val newBalance = balance + amount
      this.copy(balance = newBalance)
    case Withdrawn(amount) =>
      val newBalance = balance - amount
      this.copy(balance = newBalance)
    case MoneyTransferred(from, amount) =>
      val newBalance = balance - amount
      this.copy(balance = newBalance)
    case TransferFeeDeducted(amount) =>
      val newBalance = balance - amount
      this.copy(balance = newBalance)
    case _ => this
  }
}
