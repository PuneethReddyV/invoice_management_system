package com.tellius.invoicemanagement.persistance.tables

import java.util.UUID

import com.tellius.invoicemanagement.persistance.InvoiceManagementMySqlDriver.api._
import com.tellius.invoicemanagement.persistance.records.ItemRecord
import slick.lifted.{ProvenShape, Rep, Tag}
import com.tellius.invoicemanagement.utils.ConstantUtils._

class ItemTable(tag: Tag) extends Table[ItemRecord](tag, "item") {

  def uuid: Rep[UUID] = column[UUID](_UUID, O.PrimaryKey)
  def name: Rep[String] = column[String](NAME, O.Unique)
  def price: Rep[Double] = column[Double](PRICE)

  override def * : ProvenShape[ItemRecord] =
    (uuid, name, price) <> ((ItemRecord.apply _).tupled, ItemRecord.unapply)
}
