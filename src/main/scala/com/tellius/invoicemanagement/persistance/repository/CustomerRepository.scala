package com.tellius.invoicemanagement.persistance.repository

import java.util.UUID

import com.google.inject.Inject
import com.tellius.invoicemanagement.entities.readers.CreateNewUser
import com.tellius.invoicemanagement.persistance.records.CustomerRecord
import com.tellius.invoicemanagement.persistance.InvoiceManagementMySqlDriver.api._
import com.tellius.invoicemanagement.persistance.tables.CustomersTable

import scala.concurrent.{ExecutionContext, Future}

trait CustomerRepository {

  def save(newCustomer: CreateNewUser): Future[CustomerRecord]

  def findByUuid(uuid: UUID): Future[Option[CustomerRecord]]

  def findByUuids(uuid: Seq[UUID]): Future[Seq[CustomerRecord]]

  def findByContacts(contactNumber: String,
                     emailId: String): Future[Option[CustomerRecord]]

}

class CustomerRepositoryImpl @Inject()(val db: Database)(
  implicit ec: ExecutionContext
) extends CustomerRepository {
  val customerCollection = TableQuery[CustomersTable]

  override def save(newCustomer: CreateNewUser): Future[CustomerRecord] = {
    for {
      customerOpt <- findByContacts(
        newCustomer.contactNumber,
        newCustomer.emailId
      )
    } yield {
      customerOpt match {
        case Some(value) => Future(value)
        case _ =>
          val newRecord = CustomerRecord(
            UUID.randomUUID(),
            newCustomer.name,
            newCustomer.contactNumber,
            newCustomer.emailId
          )
          db.run(customerCollection += newRecord).map(e => newRecord)
      }
    }
  }.flatten

  override def findByUuid(uuid: UUID): Future[Option[CustomerRecord]] =
    db.run(customerCollection.filter(_.uuid === uuid).result).map(_.headOption)

  override def findByContacts(contactNumber: String,
                              emailId: String): Future[Option[CustomerRecord]] =
    db.run(
        customerCollection
          .filter(
            entity =>
              entity.contactNumber === contactNumber && entity.emailId === emailId
          )
          .result
      )
      .map(_.headOption)

  override def findByUuids(uuids: Seq[UUID]): Future[Seq[CustomerRecord]] =
    db.run(
      customerCollection
        .filter(entity => entity.uuid.inSet(uuids))
        .result
    )
}
