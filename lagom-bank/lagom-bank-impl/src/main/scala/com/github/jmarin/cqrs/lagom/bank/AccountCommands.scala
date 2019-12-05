package com.github.jmarin.cqrs.lagom.bank

import com.lightbend.lagom.scaladsl.persistence.{
  AggregateEvent,
  AggregateEventTag,
  PersistentEntity
}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{
  JsonSerializer,
  JsonSerializerRegistry
}
import play.api.libs.json._
import scala.collection.immutable.Seq

sealed trait AccountCommand[R] extends ReplyType[R]

// Open Account
case class OpenAccount(account: Account) extends AccountCommand[OpenAccountDone]

object OpenAccount {
  implicit val format: Format[OpenAccount] = Json.format
}

case class OpenAccountDone(account: Account)

object OpenAccountDone {
  implicit val format: Format[OpenAccountDone] = Json.format
}

// Deposit Money
case class Deposit(amount: BigDecimal) extends AccountCommand[DepositDone]

object Deposit {
  implicit val format: Format[Deposit] = Json.format
}

case class DepositDone(amount: BigDecimal)

object DepositDone {
  implicit val format: Format[DepositDone] = Json.format
}

// Withdraw Money
case class Withdraw(amount: BigDecimal) extends AccountCommand[WithdrawDone]

object Withdraw {
  implicit val format: Format[Withdraw] = Json.format
}

case class WithdrawDone(amount: BigDecimal)

object WithdrawDone {
  implicit val format: Format[WithdrawDone] = Json.format
}

// Transfer Money
case class TransferMoney(to: String, amount: BigDecimal)
    extends AccountCommand[TransferMoneyDone]

object TransferMoney {
  implicit val format: Format[TransferMoney] = Json.format
}

case class TransferMoneyDone(accountId: String, amount: BigDecimal)

object TransferMoneyDone {
  implicit val format: Format[TransferMoneyDone] = Json.format
}

// Get State
case object Get extends AccountCommand[AccountState] {
  implicit val format: Format[Get.type] = Format(
    Reads(_ => JsSuccess(Get)),
    Writes(_ => Json.obj())
  )
}
