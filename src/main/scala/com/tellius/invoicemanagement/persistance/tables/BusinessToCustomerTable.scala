package com.tellius.invoicemanagement.persistance.tables

import java.util.UUID

import com.tellius.invoicemanagement.persistance.records._
import com.tellius.invoicemanagement.utils.ConstantUtils._
import com.tellius.invoicemanagement.persistance.InvoiceManagementMySqlDriver.api._

import slick.lifted.{ForeignKeyQuery, ProvenShape, Rep, Tag}

class BusinessToCustomerTable(tag: Tag)
    extends Table[BusinessToCustomerRecord](tag, "business_to_customers") {

  val businessTable = TableQuery[BusinessTable]
  val customerTable = TableQuery[CustomersTable]

  def id: Rep[Int] = column[Int](ID, O.PrimaryKey, O.AutoInc)
  def businessUuid: Rep[UUID] = column[UUID](BUSINESS_UUID)
  def customerUuid: Rep[UUID] = column[UUID](CUSTOMER_UUID)
  def remainderEnabled: Rep[Boolean] = column[Boolean](REMAINDER_ENABLED)

  def businessUuidFk1: ForeignKeyQuery[BusinessTable, BusinessRecord] =
    foreignKey("business_uuid_fk3", businessUuid, businessTable)(_.uuid)

  def userUuidFk2: ForeignKeyQuery[CustomersTable, CustomerRecord] =
    foreignKey("customer_uuid_fk2", customerUuid, customerTable)(_.uuid)

  override def * : ProvenShape[BusinessToCustomerRecord] =
    (id, businessUuid, customerUuid, remainderEnabled) <> ((BusinessToCustomerRecord.apply _).tupled, BusinessToCustomerRecord.unapply)
}
