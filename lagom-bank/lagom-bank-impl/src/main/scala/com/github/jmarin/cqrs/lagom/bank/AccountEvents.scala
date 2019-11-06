package com.github.jmarin.cqrs.lagom.bank

import play.api.libs.json.{Format, Json}

sealed trait AccountEvent

case class AccountOpened(account: Account) extends AccountEvent

object AccountOpened {
  implicit val format: Format[AccountOpened] = Json.format
}

case class Deposited(amount: BigDecimal) extends AccountEvent

object Deposited {
  implicit val format: Format[Deposited] = Json.format
}

case class Withdrawn(amount: BigDecimal) extends AccountEvent

object Withdrawn {
  implicit val format: Format[Withdrawn] = Json.format
}

case class MoneyTransferred(from: String, amount: BigDecimal)
    extends AccountEvent

object MoneyTransferred {
  implicit val format: Format[MoneyTransferred] = Json.format
}

case class TransferFeeDeducted(amount: BigDecimal) extends AccountEvent

object TransferFeeDeducted {
  implicit val format: Format[TransferFeeDeducted] = Json.format
}
