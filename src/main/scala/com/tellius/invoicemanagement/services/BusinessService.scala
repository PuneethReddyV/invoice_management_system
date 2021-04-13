package com.tellius.invoicemanagement.services

import com.google.inject.Inject
import com.tellius.invoicemanagement.entities.readers.CreateNewBusinessEntiy
import com.tellius.invoicemanagement.entities.writes.CreatedResource
import com.tellius.invoicemanagement.persistance.repository.BusinessRepository

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

trait BusinessService {
  def createNewBusinessResource(
    createNewBusinessUser: CreateNewBusinessEntiy
  ): Future[CreatedResource]
}

class BusinessServiceImpl @Inject()(businessRepo: BusinessRepository)
    extends BusinessService {

  implicit val ec: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(new java.util.concurrent.ForkJoinPool())

  override def createNewBusinessResource(
    createNewBusinessUser: CreateNewBusinessEntiy
  ): Future[CreatedResource] = {
    businessRepo
      .save(createNewBusinessUser.name)
      .map(entity => CreatedResource(entity.uuid))
  }

}
