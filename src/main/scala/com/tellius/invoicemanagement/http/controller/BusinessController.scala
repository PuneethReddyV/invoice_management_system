package com.tellius.invoicemanagement.http.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.google.inject.Inject
import com.tellius.invoicemanagement.entities.readers.{
  CreateNewBusinessEntityJsonProtocol,
  CreateNewBusinessEntiy
}
import com.tellius.invoicemanagement.entities.writes.CreatedResourceResponseJsonProtocol
import com.tellius.invoicemanagement.injection.Routable
import com.tellius.invoicemanagement.services.BusinessService
import com.tellius.invoicemanagement.utils.ConstantUtils.{
  BUSINESS,
  VERSION_NUMBER
}
import com.typesafe.scalalogging.StrictLogging

class BusinessController @Inject()(businessService: BusinessService)
    extends Routable
    with StrictLogging
    with CreateNewBusinessEntityJsonProtocol
    with CreatedResourceResponseJsonProtocol {
  override def getRoutes: Route = createBusinessEntity

  private[this] val createBusinessEntity: Route = post {
    path(VERSION_NUMBER / BUSINESS) {
      entity(as[CreateNewBusinessEntiy]) { newBusiness =>
        logger.info(s"Business Entity POST for name = ${newBusiness.name}")
        complete(businessService.createNewBusinessResource(newBusiness))
      }
    }
  }
}
