package com.github.jmarin.cqrs.lagom.bank

import play.api.libs.json.{Format, Json}
import play.api.libs.json.OFormat

case class CreateAccount(accountId: String, initialBalance: BigDecimal = 0)

object CreateAccount {
    implicit val format: OFormat[CreateAccount] = Json.format
}

case class DepositMoney(amount: BigDecimal)

object DepositMoney {
    implicit val format: OFormat[DepositMoney] = Json.format
}

case class WithdrawMoney(amount: BigDecimal)

object WithdrawMoney {
    implicit val format: OFormat[WithdrawMoney] = Json.format
}

case class TransferToAccount(to: String, amount: BigDecimal)

object TransferToAccount {
    implicit val format: OFormat[TransferToAccount] = Json.format
}