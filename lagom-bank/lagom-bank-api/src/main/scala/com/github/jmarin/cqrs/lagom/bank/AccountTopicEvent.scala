package com.github.jmari.cqrs.lagom.bank

import play.api.libs.json._

sealed trait AccountTopicEvent {
  def accountId: String
}

case class UnknownEvent(accountId: String) extends AccountTopicEvent

object UnknownEvent {
  implicit val format: Format[UnknownEvent] = Json.format[UnknownEvent]
}

case class FeeTransfered(
    accountId: String,
    //toAccountId: String,
    amount: BigDecimal
) extends AccountTopicEvent

object FeeTransfered {
  implicit val format: Format[FeeTransfered] = Json.format[FeeTransfered]
}

object AccountTopicEvent {

  implicit val reads: Reads[AccountTopicEvent] = {
    (__ \ "event_type").read[String].flatMap {
      case "feeTransferred" => implicitly[Reads[FeeTransfered]].map(identity)
      case "unknownEvent"   => implicitly[Reads[UnknownEvent]].map(identity)
    }
  }

  implicit val writes: Writes[AccountTopicEvent] = Writes { event =>
    val (jsValue, eventType) = event match {
      case f: FeeTransfered =>
        (Json.toJson(f)(FeeTransfered.format), "feeTransferred")
      case u: UnknownEvent =>
        (Json.toJson(u)(UnknownEvent.format), "unknownEvent")
    }
    jsValue
      .transform(
        __.json.update((__ \ 'event_type).json.put(JsString(eventType)))
      )
      .get
  }
}
