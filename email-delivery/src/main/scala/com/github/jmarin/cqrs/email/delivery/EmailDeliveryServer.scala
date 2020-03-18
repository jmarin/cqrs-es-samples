package com.github.jmarin.cqrs.email.delivery

import akka.actor.ActorSystem
import scala.concurrent.Future
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import akka.stream.Materializer
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import com.github.jmarin.cqrs.email.delivery.protobuf.EmailServiceHandler
import akka.http.scaladsl.HttpConnectionContext
import akka.http.scaladsl.UseHttp2.Always

object EmailDeliveryServer {
  def main(args: Array[String]): Unit = {
    // important to enable HTTP/2 in ActorSystem's config
    val conf = ConfigFactory
      .parseString("akka.http.server.preview.enable-http2 = on")
      .withFallback(ConfigFactory.defaultApplication())
    val system: ActorSystem = ActorSystem("EmailDelivery", conf)
    new EmailDeliveryServer(system).run()
  }
}

class EmailDeliveryServer(system: ActorSystem) {

  def run(): Future[Http.ServerBinding] = {
    implicit val sys = system
    implicit val mat: Materializer = ActorMaterializer()
    implicit val ec: ExecutionContext = sys.dispatcher

    val service: HttpRequest => Future[HttpResponse] =
      EmailServiceHandler(new EmailServiceImpl(mat))

    val bound = Http().bindAndHandleAsync(
      service,
      interface = "127.0.0.1",
      port = 8080,
      connectionContext = HttpConnectionContext(http2 = Always)
    )

    bound.foreach { binding =>
      println(s"Email Delivery gRPC server bound to: ${binding.localAddress}")
    }

    bound
  }

}
