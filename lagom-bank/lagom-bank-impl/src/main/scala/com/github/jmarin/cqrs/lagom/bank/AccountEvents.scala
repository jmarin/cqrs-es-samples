package com.github.jmarin.cqrs.lagom.bank

import play.api.libs.json.OFormat
import play.api.libs.json.Json

sealed trait AccountEvent 

case class AccountOpened(account: Account) extends AccountEvent

object AccountOpened {
    implicit val format: OFormat[AccountOpened] = Json.format
}

case class Deposited(amount: BigDecimal) extends AccountEvent

object Deposited {
    implicit val format: OFormat[Deposited] = Json.format
}

case class Withdrawn(amount: BigDecimal) extends AccountEvent

object Withdrawn {
    implicit val format: OFormat[Withdrawn] = Json.format
}

case class MoneyTransferred(to: String, amount: BigDecimal) extends AccountEvent

object MoneyTransferred {
    implicit val format: OFormat[MoneyTransferred] = Json.format
}

case class TransferFeeDeducted(amount: BigDecimal) extends AccountEvent

object TransferFeeDeducted {
    implicit val format: OFormat[TransferFeeDeducted] = Json.format
}