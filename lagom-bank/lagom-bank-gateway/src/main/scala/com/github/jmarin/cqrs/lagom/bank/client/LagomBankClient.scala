package com.github.jmarin.cqrs.lagom.bank.client

import scala.concurrent.Future
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.RequestEntity
import akka.http.scaladsl.model.HttpMethod

object LagomBankClient {

  def sendRequest(
      uri: String,
      requestEntity: RequestEntity,
      httpMethod: HttpMethod
  )(implicit system: ActorSystem): Future[HttpResponse] = {
    println(s"Proxying request to $uri")
    Http()
      .singleRequest(
        HttpRequest(
          method = httpMethod,
          uri = uri,
          entity = requestEntity
        )
      )
  }
}
