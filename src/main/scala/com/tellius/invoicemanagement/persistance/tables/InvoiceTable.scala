package com.tellius.invoicemanagement.persistance.tables

import java.time.LocalDate
import java.util.UUID

import com.tellius.invoicemanagement.entities.eums.InvoiceStatus
import com.tellius.invoicemanagement.persistance.InvoiceManagementMySqlDriver.api._
import com.tellius.invoicemanagement.persistance.records.{
  BusinessRecord,
  CustomerRecord,
  InvoiceRecord
}
import com.tellius.invoicemanagement.utils.ConstantUtils._
import slick.lifted.{ForeignKeyQuery, ProvenShape, Rep, Tag}

class InvoiceTable(tag: Tag) extends Table[InvoiceRecord](tag, "invoice") {
  val businessTable = TableQuery[BusinessTable]
  val customerTable = TableQuery[CustomersTable]
  def uuid: Rep[UUID] = column[UUID](_UUID, O.PrimaryKey)
  def businessUuid: Rep[UUID] = column[UUID](BUSINESS_UUID)
  def customerUuid: Rep[UUID] = column[UUID](CUSTOMER_UUID)
  def amount: Rep[Double] = column[Double](AMOUNT)
  def paymentStatus: Rep[InvoiceStatus] = column[InvoiceStatus](PAYMENT_STATUS)
  def dueDate: Rep[Option[LocalDate]] = column[Option[LocalDate]](DUE_DATE)
  def isDeleted: Rep[Boolean] = column[Boolean](IS_DELETED)

  def businessUuidFk1: ForeignKeyQuery[BusinessTable, BusinessRecord] =
    foreignKey("business_uuid_fk2", businessUuid, businessTable)(_.uuid)

  def userUuidFk2: ForeignKeyQuery[CustomersTable, CustomerRecord] =
    foreignKey("user_uuid_fk2", customerUuid, customerTable)(_.uuid)

  override def * : ProvenShape[InvoiceRecord] =
    (
      uuid,
      businessUuid,
      customerUuid,
      amount,
      paymentStatus,
      dueDate,
      isDeleted
    ) <> ((InvoiceRecord.apply _).tupled, InvoiceRecord.unapply)
}
