package com.github.jmarin.cqrs.lagom.bank

import play.api.libs.json.OFormat
import play.api.libs.json.Json

case class Account(id: String, balance: BigDecimal)

object Account {
    implicit val format: OFormat[Account] = Json.format
}
