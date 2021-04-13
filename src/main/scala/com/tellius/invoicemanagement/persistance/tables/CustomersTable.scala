package com.tellius.invoicemanagement.persistance.tables

import java.util.UUID

import com.tellius.invoicemanagement.persistance.records.CustomerRecord
import slick.lifted.{ProvenShape, Rep, Tag}
import com.tellius.invoicemanagement.persistance.InvoiceManagementMySqlDriver.api._
import com.tellius.invoicemanagement.utils.ConstantUtils._

class CustomersTable(tag: Tag) extends Table[CustomerRecord](tag, "customer") {

  def uuid: Rep[UUID] = column[UUID](_UUID, O.PrimaryKey)
  def name: Rep[String] = column[String](NAME)
  def contactNumber: Rep[String] = column[String](CONTACT_NUMBER, O.Unique)
  def emailId: Rep[String] = column[String](EMAIL_ADDRESS, O.Unique)

  override def * : ProvenShape[CustomerRecord] =
    (uuid, name, contactNumber, emailId) <> ((CustomerRecord.apply _).tupled, CustomerRecord.unapply)
}
