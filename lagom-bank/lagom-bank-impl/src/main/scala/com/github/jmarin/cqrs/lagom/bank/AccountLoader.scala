package com.github.jmarin.cqrs.lagom.bank

import com.lightbend.lagom.scaladsl.server.LagomApplicationLoader
import com.lightbend.lagom.scaladsl.server.{
  LagomApplication,
  LagomApplicationContext
}
import com.lightbend.lagom.scaladsl.api.Descriptor
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import play.api.libs.ws.ahc.AhcWSComponents
import com.lightbend.lagom.scaladsl.server.LagomServer
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.softwaremill.macwire._
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.persistence.jdbc.ReadSideJdbcPersistenceComponents
import play.api.db.HikariCPComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.WriteSideCassandraPersistenceComponents
// import com.github.jmarin.cqrs.lagom.bank.readside.{
//   AccountRepository,
//   AccountProcessor
// }

class AccountLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new AccountApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new AccountApplication(context) with LagomDevModeComponents

  override def describeService: Option[Descriptor] =
    Some(readDescriptor[AccountService])
}

abstract class AccountApplication(context: LagomApplicationContext)
    extends LagomApplication(context)
    with ReadSideJdbcPersistenceComponents
    with HikariCPComponents
    // Mixing in WriteSideCassandraPersistenceComponents instead of CassandraPersistenceComponents
    // we can control what read-side to mix in
    with WriteSideCassandraPersistenceComponents
    with AhcWSComponents {

  override lazy val lagomServer: LagomServer = 
    serverFor[AccountService](wire[AccountServiceImpl])

  persistentEntityRegistry.register(wire[AccountEntity])

  override def jsonSerializerRegistry: JsonSerializerRegistry = 
    AccountSerializerRegistry

  //lazy val eventProcessor = wire[AccountProcessor]
  //lazy val repo = wire[AccountRepository]

}
