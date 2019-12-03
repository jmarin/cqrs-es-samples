package com.github.jmarin.cqrs.lagom.bank

import com.lightbend.lagom.scaladsl.server.LagomApplicationContext
import com.lightbend.lagom.scaladsl.server.LagomApplication
import play.api.libs.ws.ahc.AhcWSComponents
import com.lightbend.lagom.scaladsl.server.LagomServer
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.softwaremill.macwire._
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry

abstract class AccountApplication(context: LagomApplicationContext)
    extends LagomApplication(context)
    with CassandraPersistenceComponents
    with AhcWSComponents {

  override lazy val lagomServer: LagomServer =
    serverFor[AccountService](wire[AccountServiceImpl])

  override def jsonSerializerRegistry: JsonSerializerRegistry =
    AccountSerializerRegistry

  persistentEntityRegistry.register(wire[AccountEntity])

}
