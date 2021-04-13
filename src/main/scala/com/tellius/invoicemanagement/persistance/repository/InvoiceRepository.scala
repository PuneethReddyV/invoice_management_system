package com.tellius.invoicemanagement.persistance.repository

import java.util.UUID

import com.google.inject.Inject
import com.tellius.invoicemanagement.entities.eums.InvoiceStatus
import com.tellius.invoicemanagement.entities.readers.{
  CreateNewInvoice,
  InvoicedItems
}
import com.tellius.invoicemanagement.persistance.InvoiceManagementMySqlDriver.api._
import com.tellius.invoicemanagement.persistance.records._
import com.tellius.invoicemanagement.persistance.tables.{
  BusinessToCustomerTable,
  InvoiceTable,
  ItemsToInvoicesTable
}

import scala.concurrent.{ExecutionContext, Future}

trait InvoiceRepository {
  def save(createNewInvoice: CreateNewInvoice): Future[Option[UUID]]
  def findByCustomerUuidPaymentStatus(
    uuid: UUID,
    paymentStatus: InvoiceStatus
  ): Future[Seq[InvoiceRecord]]
  def findByBusinessUuidPaymentStatus(
    uuid: UUID,
    paymentStatus: InvoiceStatus
  ): Future[Seq[InvoiceRecord]]
}

class InvoiceRepositoryImpl @Inject()(
  val db: Database,
  businessToCustomerRepo: BusinessToCustomerRepository
)(implicit ec: ExecutionContext)
    extends InvoiceRepository {

  val invoiceCollection = TableQuery[InvoiceTable]
  val itemsToInvoiceCollection = TableQuery[ItemsToInvoicesTable]
  val businessToCustomerCollection = TableQuery[BusinessToCustomerTable]

  override def save(createNewInvoice: CreateNewInvoice): Future[Option[UUID]] = {
    val resourceUuid = UUID.randomUUID()
    for {
      custToBusiOpt <- businessToCustomerRepo.findByBusinessUuidAndCustomerUuid(
        createNewInvoice.businessUuid,
        createNewInvoice.customerUuid
      )
      updatedCount = custToBusiOpt match {
        case Some(_) =>
          db.run(
            DBIO
              .sequence(
                createInvoicesRecord(resourceUuid, createNewInvoice) ++ createItemsRecordsOfInvoice(
                  resourceUuid,
                  createNewInvoice.items
                )
              )
              .transactionally
          )
        case None =>
          db.run(
            DBIO
              .sequence(
                createInvoicesRecord(resourceUuid, createNewInvoice) ++ createItemsRecordsOfInvoice(
                  resourceUuid,
                  createNewInvoice.items
                ) ++ createBusinessToCustomerRecord(createNewInvoice)
              )
              .transactionally
          )
      }
      totalInsertedRecords = updatedCount.map(e => e.sum)
    } yield {
      totalInsertedRecords.collect {
        case size if size > 0 => Some(resourceUuid)
        case _                => None
      }
    }
  }.flatten

  override def findByCustomerUuidPaymentStatus(
    uuid: UUID,
    paymentStatus: InvoiceStatus
  ): Future[Seq[InvoiceRecord]] =
    db.run(
      invoiceCollection
        .filter(
          e => e.customerUuid === uuid && e.paymentStatus === paymentStatus
        )
        .result
    )

  override def findByBusinessUuidPaymentStatus(
    uuid: UUID,
    paymentStatus: InvoiceStatus
  ): Future[Seq[InvoiceRecord]] =
    db.run(
      invoiceCollection
        .filter(
          e => e.businessUuid === uuid && e.paymentStatus === paymentStatus
        )
        .result
    )

  private def createItemsRecordsOfInvoice(invoiceUuid: UUID,
                                          items: Seq[InvoicedItems]) =
    items.map(
      e =>
        itemsToInvoiceCollection += ItemsToInvoiceRecord(
          0,
          invoiceUuid,
          e.itemUuid,
          e.quantity
      )
    )

  private def createInvoicesRecord(resourceUuid: UUID,
                                   createNewInvoice: CreateNewInvoice) = Seq(
    invoiceCollection.insertOrUpdate(
      InvoiceRecord(
        resourceUuid,
        createNewInvoice.businessUuid,
        createNewInvoice.customerUuid,
        createNewInvoice.amount,
        createNewInvoice.paymentStatus,
        createNewInvoice.dueDate,
        isDeleted = false
      )
    )
  )

  private def createBusinessToCustomerRecord(
    createNewInvoice: CreateNewInvoice
  ) = Seq(
    businessToCustomerCollection += BusinessToCustomerRecord(
      0,
      createNewInvoice.businessUuid,
      createNewInvoice.customerUuid,
      remainderEnabled = true
    )
  )

}
