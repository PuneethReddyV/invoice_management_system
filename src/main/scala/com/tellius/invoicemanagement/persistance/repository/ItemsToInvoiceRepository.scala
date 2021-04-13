package com.tellius.invoicemanagement.persistance.repository

import java.util.UUID

import com.google.inject.Inject
import com.tellius.invoicemanagement.persistance.records.ItemsToInvoiceRecord
import com.tellius.invoicemanagement.persistance.InvoiceManagementMySqlDriver.api._
import com.tellius.invoicemanagement.persistance.tables.ItemsToInvoicesTable

import scala.concurrent.{ExecutionContext, Future}

trait ItemsToInvoiceRepository {
  def save(invoiceUuid: UUID, itemUuid: UUID, quantity: Int): Future[UUID]

  def findByInvoiceUuid(invoiceUuid: UUID): Future[Seq[ItemsToInvoiceRecord]]

  def findByInvoiceUuids(
    invoiceUuid: Seq[UUID]
  ): Future[Seq[ItemsToInvoiceRecord]]
}

class ItemsToInvoiceRepositoryImpl @Inject()(val db: Database)(
  implicit ec: ExecutionContext
) extends ItemsToInvoiceRepository {

  val itemsToInvoiceCollection = TableQuery[ItemsToInvoicesTable]

  override def save(invoiceUuid: UUID,
                    itemUuid: UUID,
                    quantity: Int): Future[UUID] = {
    val resourceUuid = UUID.randomUUID()
    db.run(
        itemsToInvoiceCollection += ItemsToInvoiceRecord(
          0,
          invoiceUuid,
          itemUuid,
          quantity
        )
      )
      .map(_ => resourceUuid)
  }

  override def findByInvoiceUuid(
    invoiceUuid: UUID
  ): Future[Seq[ItemsToInvoiceRecord]] = {
    db.run(
      itemsToInvoiceCollection.filter(_.invoiceUuid === invoiceUuid).result
    )
  }

  override def findByInvoiceUuids(
    invoiceUuids: Seq[UUID]
  ): Future[Seq[ItemsToInvoiceRecord]] = {
    db.run(
      itemsToInvoiceCollection.filter(_.invoiceUuid.inSet(invoiceUuids)).result
    )
  }
}
