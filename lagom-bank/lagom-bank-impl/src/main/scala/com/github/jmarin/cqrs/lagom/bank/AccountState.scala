package com.github.jmarin.cqrs.lagom.bank

import play.api.libs.json.OFormat
import play.api.libs.json.Json

case class AccountState(open: Boolean = false, balance: BigDecimal) {
    def applyEvent(event: AccountEvent): AccountState = event match {
        case AccountOpened(account) => 
           AccountState(true, account.balance) 
        case Deposited(amount) => 
           val newBalance = this.balance + amount
           this.copy(balance = newBalance)
        case MoneyTransferred(to, amount) => 
          val newBalance = this.balance - amount
          this.copy(balance = newBalance)
        case TransferFeeDeducted(amount) => 
          val newBalance = this.balance - amount
          this.copy(balance = newBalance)
        case Withdrawn(amount) => 
          val newBalance = this.balance - amount
          this.copy(balance = newBalance)
    }
}

object AccountState {
    implicit val format: OFormat[AccountState] = Json.format
}

object EmptyState {
    def apply(): AccountState = AccountState(false, 0)
}