package com.github.jmari.cqrs.lagom.bank

import play.api.libs.json.Format
import play.api.libs.json.Json

case class FeeTransfered(
    fromAccountId: String,
    toAccountId: String,
    amount: BigDecimal
)

object FeeTransfered {
  implicit val format: Format[FeeTransfered] = Json.format[FeeTransfered]
}
