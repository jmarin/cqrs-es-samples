package com.github.jmarin.cqrs.lagom.bank

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{OFormat, Json}
import play.api.libs.json.Format
import play.api.libs.json.JsSuccess
import play.api.libs.json.Reads
import play.api.libs.json.Writes

sealed trait AccountCommand[R] extends ReplyType[R]

case class OpenAccount(account: Account) extends AccountCommand[OpenAccountDone]

object OpenAccount {
    implicit val format: OFormat[OpenAccount] = Json.format
}

case class OpenAccountDone(account: Account)

object OpenAccountDone {
    implicit val format: OFormat[OpenAccountDone] = Json.format
}

case class Withdraw(amount: BigDecimal) extends AccountCommand[WithdrawDone]

object Withdraw {
    implicit val format: OFormat[Withdraw] = Json.format
}

case class WithdrawDone(amount: BigDecimal)

object WithdrawDone {
    implicit val format: OFormat[WithdrawDone] = Json.format
}

case class TransferMoney(to: String, amount: BigDecimal) extends AccountCommand[TransferMoneyDone]

object TransferMoney {
    implicit val format: OFormat[TransferMoney] = Json.format
}

case class TransferMoneyDone(accountId: String, amount: BigDecimal)

object TransferMoneyDone {
    implicit val format: OFormat[TransferMoneyDone] = Json.format
}

case object Get extends AccountCommand[AccountState] {
    implicit val format: Format[Get.type] = Format(
        Reads(_ => JsSuccess(Get)),
        Writes(_ => Json.obj())
    )
}