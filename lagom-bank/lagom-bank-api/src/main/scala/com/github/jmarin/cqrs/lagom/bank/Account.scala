package com.github.jmarin.cqrs.lagom.bank

import play.api.libs.json.{Format, Json}

case class Account(id: String, balance: BigDecimal)

object Account {
  implicit val format: Format[Account] = Json.format
}
