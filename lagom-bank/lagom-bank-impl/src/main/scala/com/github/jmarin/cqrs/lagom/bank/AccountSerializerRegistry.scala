package com.github.jmarin.cqrs.lagom.bank

import com.lightbend.lagom.scaladsl.playjson.{
  JsonSerializer,
  JsonSerializerRegistry
}

import scala.collection.immutable.Seq

object AccountSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[Get.type],
    JsonSerializer[OpenAccount],
    JsonSerializer[OpenAccountDone],
    JsonSerializer[Deposit],
    JsonSerializer[DepositDone],
    JsonSerializer[Withdraw],
    JsonSerializer[WithdrawDone],
    JsonSerializer[TransferMoney],
    JsonSerializer[TransferMoneyDone],
    JsonSerializer[AccountOpened],
    JsonSerializer[Deposited],
    JsonSerializer[Withdrawn],
    JsonSerializer[TransferFeeDeducted],
    JsonSerializer[MoneyTransferred],
    JsonSerializer[AccountState],
    JsonSerializer[AccountException]
  )
}
