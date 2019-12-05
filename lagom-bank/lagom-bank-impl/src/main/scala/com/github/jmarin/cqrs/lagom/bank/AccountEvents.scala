package com.github.jmarin.cqrs.lagom.bank

import play.api.libs.json.{Format, Json}
import com.lightbend.lagom.scaladsl.persistence.AggregateEvent
import com.lightbend.lagom.scaladsl.persistence.AggregateEventTagger
import com.lightbend.lagom.scaladsl.persistence.AggregateEventTag

sealed trait AccountEvent extends AggregateEvent[AccountEvent] {
  def aggregateTag = AccountEvent.Tag
}

object AccountEvent {
  val Tag = AggregateEventTag.sharded[AccountEvent](10)
}

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

case class MoneyTransferred(to: String, amount: BigDecimal) extends AccountEvent

object MoneyTransferred {
  implicit val format: Format[MoneyTransferred] = Json.format
}

case class TransferFeeDeducted(amount: BigDecimal) extends AccountEvent

object TransferFeeDeducted {
  implicit val format: Format[TransferFeeDeducted] = Json.format
}
