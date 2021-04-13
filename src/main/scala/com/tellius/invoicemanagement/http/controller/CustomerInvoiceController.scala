package com.tellius.invoicemanagement.http.controller

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.google.inject.Inject
import com.tellius.invoicemanagement.common.QueryParametersExtractor
import com.tellius.invoicemanagement.entities.writes.CustomerInvoicesJsonProtocol
import com.tellius.invoicemanagement.injection.Routable
import com.tellius.invoicemanagement.services.NotificationsService
import com.tellius.invoicemanagement.utils.ConstantUtils._
import com.typesafe.scalalogging.StrictLogging

class CustomerInvoiceController @Inject()(
  notifcationService: NotificationsService
) extends Routable
    with StrictLogging
    with QueryParametersExtractor
    with CustomerInvoicesJsonProtocol {
  override def getRoutes: Route = getCustomerNotifications

  private[this] val getCustomerNotifications: Route = get {
    path(VERSION_NUMBER / CUSTOMER_INVOICE / JavaUUID) { customerUuid =>
      extract(_.request.uri.query()) { queryParametersString: Uri.Query =>
        logger.info(s"Get Customer Invoices for ${customerUuid}")
        complete(
          notifcationService
            .getCustomerInvoices(
              customerUuid,
              convertQueryParamsToInvoiceQueryParams(queryParametersString)
            )
        )
      }
    }
  }
}
