package com.tellius.invoicemanagement.persistance.repository

import java.util.UUID

import com.tellius.invoicemanagement.persistance.InvoiceManagementMySqlDriver.api._
import com.google.inject.Inject
import com.tellius.invoicemanagement.persistance.records.BusinessRecord
import com.tellius.invoicemanagement.persistance.tables.BusinessTable
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

trait BusinessRepository {

  def save(name: String): Future[BusinessRecord]

  def findByUuid(uuid: UUID): Future[Option[BusinessRecord]]

  def findByUuids(uuid: Seq[UUID]): Future[Seq[BusinessRecord]]

  def findByName(name: String): Future[Option[BusinessRecord]]

}

class BusinessRepositoryImpl @Inject()(val db: Database)(
  implicit ec: ExecutionContext
) extends BusinessRepository {

  val businessCollection = TableQuery[BusinessTable]

  override def save(name: String): Future[BusinessRecord] = {
    for {
      recordOpt <- findByName(name)
    } yield {
      recordOpt match {
        case Some(record) => Future(record)
        case None =>
          val newRecord = BusinessRecord(UUID.randomUUID(), name)
          db.run(businessCollection += newRecord).map(_ => newRecord)
      }
    }
  }.flatten

  override def findByUuid(uuid: UUID): Future[Option[BusinessRecord]] =
    db.run(businessCollection.filter(_.uuid === uuid).result).map(_.headOption)

  override def findByName(name: String): Future[Option[BusinessRecord]] =
    db.run(businessCollection.filter(_.name === name).result).map(_.headOption)

  override def findByUuids(uuids: Seq[UUID]): Future[Seq[BusinessRecord]] =
    db.run(businessCollection.filter(_.uuid.inSet(uuids)).result)
}
