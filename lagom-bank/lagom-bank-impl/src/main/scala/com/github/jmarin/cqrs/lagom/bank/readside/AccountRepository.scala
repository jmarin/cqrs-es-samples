package com.github.jmarin.cqrs.lagom.bank.readside

import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcSession
import com.lightbend.lagom.scaladsl.persistence.ReadSide
import scala.concurrent.{ExecutionContext, Future}
import com.github.jmarin.cqrs.lagom.bank.Account
import java.sql.{Connection, ResultSet}

case class ReadSideAccount(id: String, balance: BigDecimal, created: String)

class AccountRepository(
    session: JdbcSession,
    readSide: ReadSide,
    eventProcessor: AccountProcessor
)(
    implicit ec: ExecutionContext
) {

  readSide.register(eventProcessor)

  def getAll(): Future[Seq[ReadSideAccount]] = {
    session.withConnection { conn: Connection =>
      JdbcSession.tryWith(
        conn.prepareStatement("SELECT * from accounts;").executeQuery
      ) {
        parse
      }
    }
  }

  private def parse(rs: ResultSet): Seq[ReadSideAccount] = {
    val accounts = scala.collection.mutable.Buffer.empty[ReadSideAccount]
    while (rs.next()) {
      accounts += ReadSideAccount(
        rs.getString("id"),
        rs.getBigDecimal("balance"),
        rs.getString("created")
      )
    }
    accounts.toIndexedSeq
  }

}
