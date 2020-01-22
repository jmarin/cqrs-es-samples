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
import com.github.jmarin.cqrs.lagom.bank.{Deposited, Withdrawn}
import org.h2.jdbc.JdbcConnection
import com.github.jmarin.cqrs.lagom.bank.MoneyTransferred
import com.github.jmarin.cqrs.lagom.bank.TransferFeeDeducted

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
      .setEventHandler[Deposited](deposited)
      .setEventHandler[Withdrawn](withdrawn)
      .setEventHandler(transferred)
      .setEventHandler(feeDeducted)
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

  private def deposited(
      connection: Connection,
      eventElement: EventStreamElement[Deposited]
  ): Unit = {
    JdbcSession.tryWith(
      connection
        .prepareStatement(
          "UPDATE accounts SET balance = balance + ? WHERE id = ?"
        )
    ) { statement =>
      statement.setBigDecimal(1, eventElement.event.amount.bigDecimal)
      statement.setString(2, eventElement.entityId)
      statement.executeUpdate
    }
  }

  private def withdrawn(
      connection: Connection,
      eventElement: EventStreamElement[Withdrawn]
  ): Unit = {
    JdbcSession.tryWith(
      connection
        .prepareStatement(
          "UPDATE accounts SET balance = balance - ? WHERE id = ?"
        )
    ) { statement =>
      statement.setBigDecimal(1, eventElement.event.amount.bigDecimal)
      statement.setString(2, eventElement.entityId)
      statement.executeUpdate
    }
  }

  private def transferred(
      connection: Connection,
      eventElement: EventStreamElement[MoneyTransferred]
  ): Unit = {
    JdbcSession.tryWith(
      connection
        .prepareStatement(
          "UPDATE accounts SET balance = balance - ? WHERE id = ?"
        )
    ) { statement =>
      statement.setBigDecimal(1, eventElement.event.amount.bigDecimal)
      statement.setString(2, eventElement.entityId)
      statement.executeUpdate
    }
  }

  private def feeDeducted(
      connection: Connection,
      eventElement: EventStreamElement[TransferFeeDeducted]
  ): Unit = {
    JdbcSession.tryWith(
      connection.prepareStatement(
        "UPDATE accounts SET balance = balance - ? WHERE id = ?"
      )
    ) { statement =>
      statement.setBigDecimal(1, eventElement.event.amount.bigDecimal)
      statement.setString(2, eventElement.entityId)
      statement.executeUpdate
    }
  }
}
