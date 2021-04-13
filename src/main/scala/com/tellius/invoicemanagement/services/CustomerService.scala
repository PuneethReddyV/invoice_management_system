package com.tellius.invoicemanagement.services

import com.google.inject.Inject
import com.tellius.invoicemanagement.entities.readers.CreateNewUser
import com.tellius.invoicemanagement.entities.writes.CreatedResource
import com.tellius.invoicemanagement.persistance.repository.CustomerRepository

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

trait CustomerService {
  def createNewUserResource(newUser: CreateNewUser): Future[CreatedResource]
}

class CustomerServiceImpl @Inject()(customerRepo: CustomerRepository)
    extends CustomerService {

  implicit val ec: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(new java.util.concurrent.ForkJoinPool())

  override def createNewUserResource(
    newUser: CreateNewUser
  ): Future[CreatedResource] =
    customerRepo.save(newUser).map(e => CreatedResource(e.uuid))
}
