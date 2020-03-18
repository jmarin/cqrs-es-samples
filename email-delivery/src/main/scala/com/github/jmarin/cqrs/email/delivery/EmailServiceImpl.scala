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

class EmailServiceImpl(materializer: Materializer) extends EmailService {

  override def serviceInfo(
      in: ServiceInfoRequest
  ): Future[ServiceInfoResponse] = ???

  override def sendEmail(in: SendEmailRequest): Future[SendEmailResponse] = ???

}
