package com.github.jmarin.cqrs.lagom.bank

import play.api.libs.json._

// Exception thrown by Account Validation
case class AccountException(message: String) extends RuntimeException(message)

case object AccountException {
  implicit val format: Format[AccountException] = Json.format
}
