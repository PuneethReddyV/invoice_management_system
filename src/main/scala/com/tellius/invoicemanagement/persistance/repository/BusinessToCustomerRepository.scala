package com.tellius.invoicemanagement.persistance.repository

import java.util.UUID

import com.google.inject.Inject
import com.tellius.invoicemanagement.persistance.InvoiceManagementMySqlDriver.api._
import com.tellius.invoicemanagement.persistance.records.BusinessToCustomerRecord
import com.tellius.invoicemanagement.persistance.tables.BusinessToCustomerTable

import scala.concurrent.{ExecutionContext, Future}

trait BusinessToCustomerRepository {

  def save(businessUuid: UUID, customerUuid: UUID): Future[Int]

  def update(
    updatedRecord: BusinessToCustomerRecord
  ): Future[BusinessToCustomerRecord]

  def findByBusinessUuidAndCustomerUuid(
    businessUuid: UUID,
    customerUuid: UUID
  ): Future[Option[BusinessToCustomerRecord]]

  def findByCustomerUuidAndNotification(
    customerUuid: UUID,
    enabled: Boolean
  ): Future[Seq[BusinessToCustomerRecord]]

  def findByBusinessUuidAndNotification(
    customerUuid: UUID,
    enabled: Boolean
  ): Future[Seq[BusinessToCustomerRecord]]
}

class BusinessToCustomerRepositoryImpl @Inject()(val db: Database)(
  implicit ec: ExecutionContext
) extends BusinessToCustomerRepository {
  val businessToCustomerCollection = TableQuery[BusinessToCustomerTable]
  override def save(businessUuid: UUID, customerUuid: UUID): Future[Int] = {
    for {
      recordOpt <- findByBusinessUuidAndCustomerUuid(businessUuid, customerUuid)
    } yield {
      recordOpt match {
        case Some(record) => Future(record.id)
        case None =>
          db.run(
              businessToCustomerCollection += BusinessToCustomerRecord(
                0,
                businessUuid,
                customerUuid,
                remainderEnabled = true
              )
            )
            .flatMap(
              _ =>
                findByBusinessUuidAndCustomerUuid(businessUuid, customerUuid)
                  .map(_.fold(-1)(_.id))
            )
      }
    }
  }.flatten

  override def findByBusinessUuidAndCustomerUuid(
    businessUuid: UUID,
    customerUuid: UUID
  ): Future[Option[BusinessToCustomerRecord]] =
    db.run(
        businessToCustomerCollection
          .filter(
            e =>
              e.businessUuid === businessUuid && e.customerUuid === customerUuid
          )
          .result
      )
      .map(_.headOption)

  override def update(
    updatedRecord: BusinessToCustomerRecord
  ): Future[BusinessToCustomerRecord] = {
    db.run(businessToCustomerCollection.insertOrUpdate(updatedRecord))
      .map(_ => updatedRecord)
  }

  override def findByCustomerUuidAndNotification(
    customerUuid: UUID,
    enabled: Boolean
  ): Future[Seq[BusinessToCustomerRecord]] =
    db.run(
      businessToCustomerCollection
        .filter(
          e => e.customerUuid === customerUuid && e.remainderEnabled === enabled
        )
        .result
    )

  override def findByBusinessUuidAndNotification(
    businessUuid: UUID,
    enabled: Boolean
  ): Future[Seq[BusinessToCustomerRecord]] = db.run(
    businessToCustomerCollection
      .filter(
        e => e.businessUuid === businessUuid && e.remainderEnabled === enabled
      )
      .result
  )
}
