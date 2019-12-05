package com.github.jmarin.cqrs.lagom.bank.readside

import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcReadSide
import scala.concurrent.ExecutionContext
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor
import com.github.jmarin.cqrs.lagom.bank.AccountEvent
import com.lightbend.lagom.scaladsl.persistence.AggregateEventTag
import java.sql.Connection
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcSession
import com.lightbend.lagom.scaladsl.persistence.EventStreamElement
import com.github.jmarin.cqrs.lagom.bank.AccountOpened
import java.time.Instant

class AccountProcessor(readSide: JdbcReadSide)(implicit ec: ExecutionContext)
    extends ReadSideProcessor[AccountEvent] {

  val createTableSql =
    "CREATE TABLE IF NOT EXISTS accounts (id VARCHAR(255), balance DECIMAL, created VARCHAR(255), PRIMARY KEY(id))"

  val buildTables: Connection => Unit = { connection =>
    JdbcSession.tryWith(connection.createStatement()) {
      _.executeUpdate(createTableSql)
    }
  }

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[AccountEvent] =
    readSide
      .builder[AccountEvent]("AccountReadSide")
      .setGlobalPrepare(buildTables)
      .setEventHandler[AccountOpened](accountOpened)
      .build()

  override def aggregateTags: Set[AggregateEventTag[AccountEvent]] =
    AccountEvent.Tag.allTags

  private def accountOpened(
      connection: Connection,
      eventElement: EventStreamElement[AccountOpened]
  ): Unit = {
    JdbcSession.tryWith(
      connection
        .prepareStatement(
          "INSERT INTO accounts(id, balance, created) VALUES (?, ?, ?)"
        )
    ) { statement =>
      statement.setString(1, eventElement.entityId)
      statement.setBigDecimal(2, eventElement.event.account.balance.bigDecimal)
      statement.setString(3, Instant.now().toString())
      statement.executeUpdate
    }
  }
}
