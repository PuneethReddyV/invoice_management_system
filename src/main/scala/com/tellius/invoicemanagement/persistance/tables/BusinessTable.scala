package com.tellius.invoicemanagement.persistance.tables

import java.util.UUID

import com.tellius.invoicemanagement.persistance.InvoiceManagementMySqlDriver.api._
import com.tellius.invoicemanagement.persistance.records.BusinessRecord
import slick.lifted.{ProvenShape, Rep, Tag}
import com.tellius.invoicemanagement.utils.ConstantUtils.{_UUID, NAME}

class BusinessTable(tag: Tag) extends Table[BusinessRecord](tag, "business") {

  def uuid: Rep[UUID] = column[UUID](_UUID, O.PrimaryKey)
  def name: Rep[String] = column[String](NAME, O.Unique)

  override def * : ProvenShape[BusinessRecord] =
    (uuid, name) <> ((BusinessRecord.apply _).tupled, BusinessRecord.unapply)
}
