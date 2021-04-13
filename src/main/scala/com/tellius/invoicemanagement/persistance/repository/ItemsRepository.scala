package com.tellius.invoicemanagement.persistance.repository

import java.util.UUID

import com.google.inject.Inject
import com.tellius.invoicemanagement.entities.readers.CreateNewItem
import com.tellius.invoicemanagement.persistance.InvoiceManagementMySqlDriver.api._
import com.tellius.invoicemanagement.persistance.records.{
  CustomerRecord,
  ItemRecord
}
import com.tellius.invoicemanagement.persistance.tables.ItemTable

import scala.concurrent.{ExecutionContext, Future}

trait ItemsRepository {

  def save(newItem: CreateNewItem): Future[ItemRecord]

  def findByUuid(uuid: UUID): Future[Option[ItemRecord]]

  def findByUuids(uuid: Seq[UUID]): Future[Seq[ItemRecord]]

  def findByNameAndPrice(itemName: String,
                         price: Double): Future[Option[ItemRecord]]

}

class ItemsRepositoryImpl @Inject()(val db: Database)(
  implicit ec: ExecutionContext
) extends ItemsRepository {
  val itemCollection = TableQuery[ItemTable]

  override def save(newItem: CreateNewItem): Future[ItemRecord] = {
    for {
      itemOpt <- findByNameAndPrice(newItem.name, newItem.price)
    } yield {
      itemOpt match {
        case Some(value) => Future(value)
        case _ =>
          val newRecord =
            ItemRecord(UUID.randomUUID(), newItem.name, newItem.price)
          db.run(itemCollection += newRecord).map(_ => newRecord)
      }
    }
  }.flatten

  override def findByUuid(uuid: UUID): Future[Option[ItemRecord]] =
    db.run(itemCollection.filter(_.uuid === uuid).result).map(_.headOption)

  override def findByNameAndPrice(itemName: String,
                                  price: Double): Future[Option[ItemRecord]] =
    db.run(
        itemCollection
          .filter(entity => entity.name === itemName && entity.price === price)
          .result
      )
      .map(_.headOption)

  override def findByUuids(uuids: Seq[UUID]): Future[Seq[ItemRecord]] =
    db.run(itemCollection.filter(e => e.uuid.inSet(uuids)).result)
}
