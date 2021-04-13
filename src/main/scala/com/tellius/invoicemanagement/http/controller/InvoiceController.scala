package com.tellius.invoicemanagement.http.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.google.inject.Inject
import com.tellius.invoicemanagement.entities.readers.{
  CreateNewInvoice,
  CreateNewInvoiceEntityJsonProtocol
}
import com.tellius.invoicemanagement.entities.writes.CreatedResourceResponseJsonProtocol
import com.tellius.invoicemanagement.injection.Routable
import com.tellius.invoicemanagement.services.InvoiceService
import com.tellius.invoicemanagement.utils.ConstantUtils._
import com.typesafe.scalalogging.StrictLogging

class InvoiceController @Inject()(invoiceService: InvoiceService)
    extends Routable
    with StrictLogging
    with CreateNewInvoiceEntityJsonProtocol
    with CreatedResourceResponseJsonProtocol {

  override def getRoutes: Route = createUserEntity

  private[this] val createUserEntity: Route = post {
    path(VERSION_NUMBER / INVOICE) {
      entity(as[CreateNewInvoice]) { newInvoice =>
        logger.info(s"Invoice POST for name = ${newInvoice.toString}")
        complete(invoiceService.createNewInvoicesResource(newInvoice))
      }
    }
  }
}
