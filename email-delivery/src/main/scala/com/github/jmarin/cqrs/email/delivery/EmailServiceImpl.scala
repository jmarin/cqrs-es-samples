package com.github.jmarin.cqrs.email.delivery

import akka.stream.Materializer
import com.github.jmarin.cqrs.email.delivery.protobuf.EmailService
import com.github.jmarin.cqrs.email.delivery.protobuf.{
  ServiceInfoRequest,
  ServiceInfoResponse
}
import scala.concurrent.Future
import com.github.jmarin.cqrs.email.delivery.protobuf.{
  SendEmailRequest,
  SendEmailResponse
}
import scala.concurrent.Future
import java.time.Instant

class EmailServiceImpl(materializer: Materializer) extends EmailService {

  override def serviceInfo(
      in: ServiceInfoRequest
  ): Future[ServiceInfoResponse] =
    Future.successful(ServiceInfoResponse("1.0", Instant.now().toString()))

  override def sendEmail(in: SendEmailRequest): Future[SendEmailResponse] = ???

}
