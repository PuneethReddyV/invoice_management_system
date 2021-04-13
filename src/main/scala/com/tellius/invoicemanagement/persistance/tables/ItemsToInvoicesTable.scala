package com.tellius.invoicemanagement.persistance.tables

import java.util.UUID

import com.tellius.invoicemanagement.persistance.records._
import com.tellius.invoicemanagement.persistance.InvoiceManagementMySqlDriver.api._
import com.tellius.invoicemanagement.utils.ConstantUtils._
import slick.lifted.{ForeignKeyQuery, ProvenShape, Rep, Tag}

class ItemsToInvoicesTable(tag: Tag)
    extends Table[ItemsToInvoiceRecord](tag, "items_to_invoices") {

  val itemsTable = TableQuery[ItemTable]
  val invoiceTable = TableQuery[InvoiceTable]

  def id: Rep[Int] = column[Int](ID, O.PrimaryKey, O.AutoInc)
  def invoiceUuid: Rep[UUID] = column[UUID](INVOICE_UUID)
  def itemsUuid: Rep[UUID] = column[UUID](ITEM_UUID)
  def quantity: Rep[Int] = column[Int](QUANTITY)

  def businessUuidFk1: ForeignKeyQuery[InvoiceTable, InvoiceRecord] =
    foreignKey("invoice_uuid_fk2", invoiceUuid, invoiceTable)(_.uuid)

  def userUuidFk2: ForeignKeyQuery[ItemTable, ItemRecord] =
    foreignKey("items_uuid_fk2", itemsUuid, itemsTable)(_.uuid)

  override def * : ProvenShape[ItemsToInvoiceRecord] =
    (id, invoiceUuid, itemsUuid, quantity) <> ((ItemsToInvoiceRecord.apply _).tupled, ItemsToInvoiceRecord.unapply)
}
