package com.github.jmarin.cqrs.lagom.bank

import play.api.libs.json.{Format, Json}

case class CreateAccount(accountId: String, initialBalance: BigDecimal = 0)

object CreateAccount {
  implicit val format: Format[CreateAccount] = Json.format
}

case class DepositMoney(amount: BigDecimal)

object DepositMoney {
  implicit val format: Format[DepositMoney] = Json.format
}

case class WithdrawMoney(amount: BigDecimal)

object WithdrawMoney {
  implicit val format: Format[WithdrawMoney] = Json.format
}

case class TransferToAccount(to: String, amount: BigDecimal)

object TransferToAccount {
  implicit val format: Format[TransferToAccount] = Json.format
}
