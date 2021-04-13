package com.tellius.invoicemanagement.http.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.google.inject.Inject
import com.tellius.invoicemanagement.entities.readers.{
  CreateNewUser,
  CreateNewUserJsonProtocol
}
import com.tellius.invoicemanagement.entities.writes.CreatedResourceResponseJsonProtocol
import com.tellius.invoicemanagement.injection.Routable
import com.tellius.invoicemanagement.services.CustomerService
import com.tellius.invoicemanagement.utils.ConstantUtils.{
  CUSTOMER,
  VERSION_NUMBER
}
import com.typesafe.scalalogging.StrictLogging

class CustomerController @Inject()(customerService: CustomerService)
    extends Routable
    with StrictLogging
    with CreateNewUserJsonProtocol
    with CreatedResourceResponseJsonProtocol {
  override def getRoutes: Route = createUserEntity

  private[this] val createUserEntity: Route = post {
    path(VERSION_NUMBER / CUSTOMER) {
      entity(as[CreateNewUser]) { newUser =>
        logger.info(s"User POST for name = ${newUser.toString}")
        complete(customerService.createNewUserResource(newUser))
      }
    }
  }
}
