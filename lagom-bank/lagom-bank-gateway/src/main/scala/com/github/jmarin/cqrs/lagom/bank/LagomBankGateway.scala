package com.github.jmarin.cqrs.lagom.bank

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import scala.io.StdIn
import scala.concurrent.Future
import akka.discovery.ServiceDiscovery.Resolved
import akka.discovery.Discovery
import scala.concurrent.duration._
import akka.discovery.ServiceDiscovery.ResolvedTarget
import com.github.jmarin.cqrs.lagom.bank.client.LagomBankClient
import akka.http.scaladsl.model.StatusCodes
import scala.util.Success
import scala.util.Failure
import akka.http.scaladsl.model.HttpResponse
import java.net.InetAddress
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpMethods

object LagomBankGateway {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("lagom-bank-gateway")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val discovery = Discovery(system).discovery

    val bankService: Future[Resolved] =
      discovery.lookup("lagom-bank-service", resolveTimeout = 500 milliseconds)

    val status: Route =
      path("status") {
        get {
          complete("OK")
        }
      }

    def route(uri: String): Route =
      extractRequest { request =>
        val url = request.uri.path
        val path = s"${uri}$url"
        val requestEntity = request.entity
        get {
          complete(
            LagomBankClient.sendRequest(path, requestEntity, HttpMethods.GET)
          )
        } ~
          post {
            complete(
              LagomBankClient.sendRequest(path, requestEntity, HttpMethods.POST)
            )
          } ~
          put {
            complete {
              LagomBankClient.sendRequest(path, requestEntity, HttpMethods.PUT)
            }
          }
      }

    def bindingFuture(uri: String): Future[ServerBinding] =
      Http().bindAndHandle(status ~ route(uri), "localhost", 8080)

    val futureServer = for {
      resolved <- bankService
      address = resolved.addresses.headOption
      host = address.map(_.host).getOrElse("localhost")
      port = address.map(_.port).flatten.getOrElse(9000)
      uri = s"http://${host}:${port}"
      server <- bindingFuture(uri)
    } yield {
      println(
        s"Server proxying requests to ${uri}"
      )
      server
    }
    futureServer
      .onComplete {
        case Success(x) =>
          println("Server Started")
        case Failure(e) =>
          println("Error starting gateway service")
          sys.exit(1)
      }
  }
}
