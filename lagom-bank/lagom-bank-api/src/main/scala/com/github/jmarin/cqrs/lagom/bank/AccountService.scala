package com.github.jmarin.cqrs.lagom.bank

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.Service
import com.lightbend.lagom.scaladsl.api.Descriptor
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.ServiceCall

trait AccountService extends Service {

  def createAccount: ServiceCall[CreateAccount, Account]
  def deposit(id: String): ServiceCall[DepositMoney, Account]
  def withdraw(id: String): ServiceCall[WithdrawMoney, Account]
  def get(id: String): ServiceCall[NotUsed, Account]
  def transfer(id: String): ServiceCall[TransferToAccount, Account]
  def getAll(): ServiceCall[NotUsed, Seq[Account]]

  override def descriptor: Descriptor = {
    import Service._
    named("accounts")
      .withCalls(
        restCall(Method.POST, "/accounts", createAccount),
        restCall(Method.GET, "/accounts", getAll),
        restCall(Method.PUT, "/accounts/:id/deposit", deposit _),
        restCall(Method.PUT, "/accounts/:id/withdraw", withdraw _),
        restCall(Method.PUT, "/accounts/:id/transfer", transfer _),
        restCall(Method.GET, "/accounts/:id", get _)
      )
      .withAutoAcl(true)
  }
}
