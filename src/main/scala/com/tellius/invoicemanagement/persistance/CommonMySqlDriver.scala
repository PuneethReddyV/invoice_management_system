package com.tellius.invoicemanagement.persistance

import java.sql.{PreparedStatement, ResultSet, Timestamp}
import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

import enumeratum.{Enum, EnumEntry}
import slick.ast.FieldSymbol
import slick.jdbc.{JdbcType, MySQLProfile}
import slick.lifted.CanBeQueryCondition

import scala.language.higherKinds
import scala.reflect.ClassTag

trait CommonMySqlDriver extends MySQLProfile {
  override val columnTypes = new InvoiceJdbcTypes

  class InvoiceJdbcTypes extends super.JdbcTypes {
    override val uuidJdbcType: UUIDJdbcType = new UUIDJdbcType {
      override def sqlTypeName(sym: Option[FieldSymbol]): String = "UUID"
      override def valueToSQLLiteral(value: UUID): String = "'" + value + "'"
      override def hasLiteralForm: Boolean = true

      override def setValue(v: UUID, p: PreparedStatement, idx: Int): Unit =
        p.setString(idx, toString(v))
      override def getValue(r: ResultSet, idx: Int): UUID =
        fromString(r.getString(idx))
      override def updateValue(v: UUID, r: ResultSet, idx: Int): Unit =
        r.updateString(idx, toString(v))

      //scalastyle:off null
      private def toString(uuid: UUID): String =
        if (uuid != null) uuid.toString else null
      private def fromString(uuidString: String): UUID =
        if (uuidString != null) UUID.fromString(uuidString) else null
      //scalastyle:on null
    }
  }

  trait CommonApi extends API {
    /*
      Note on the mapped type:
        There is some unexplained issue with some LocalDates incorrectly stored in DB Date column (as a day behind
        the expected one). Changing "transport" type from SQL Date to plain String seemed to solve the issue.
     */
    implicit val localDateConverter: JdbcType[LocalDate] =
      MappedColumnType.base[LocalDate, String](_.toString, LocalDate.parse)

    implicit val localDateOptionConverter: JdbcType[Option[LocalDate]] =
      MappedColumnType.base[Option[LocalDate], String](
        e => e.fold("")(_.toString),
        e => if (e.isEmpty) { None } else { Some(LocalDate.parse(e)) }
      )

    implicit val localDateTimeConverter: JdbcType[LocalDateTime] =
      MappedColumnType
        .base[LocalDateTime, Timestamp](Timestamp.valueOf, _.toLocalDateTime)

    def enumeratumColumnMapper[T <: EnumEntry](
      enum: Enum[T]
    )(implicit tag: ClassTag[T]): JdbcType[T] =
      MappedColumnType.base[T, String](e => e.entryName, s => enum.withName(s))

    implicit class FilterOps[E, U, C[_]](query: Query[E, U, C]) {

      def filterIfDefined[F, T <: Rep[_]](
        context: Option[F]
      )(f: (E, F) => T)(implicit wt: CanBeQueryCondition[T]): Query[E, U, C] = {
        context
          .map(contextValue => query.filter(f(_, contextValue)))
          .getOrElse(query)
      }
    }

  }

  object CommonApi extends CommonApi
}

object CommonMySqlDriver extends CommonMySqlDriver
