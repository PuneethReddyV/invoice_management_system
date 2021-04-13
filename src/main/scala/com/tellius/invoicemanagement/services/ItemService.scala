package com.tellius.invoicemanagement.services

import com.google.inject.Inject
import com.tellius.invoicemanagement.entities.readers.CreateNewItem
import com.tellius.invoicemanagement.entities.writes.CreatedResource
import com.tellius.invoicemanagement.persistance.repository.ItemsRepository

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

trait ItemService {
  def createNewItemResource(
    createNewItem: CreateNewItem
  ): Future[CreatedResource]
}

class ItemServiceImpl @Inject()(itemsRepo: ItemsRepository)
    extends ItemService {

  implicit val ec: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(new java.util.concurrent.ForkJoinPool())

  override def createNewItemResource(
    createNewItem: CreateNewItem
  ): Future[CreatedResource] = {
    itemsRepo
      .save(createNewItem)
      .map(entity => CreatedResource(entity.uuid))
  }

}
