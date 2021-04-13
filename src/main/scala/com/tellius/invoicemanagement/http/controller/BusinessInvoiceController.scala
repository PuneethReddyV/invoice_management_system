package com.tellius.invoicemanagement.http.controller

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.google.inject.Inject
import com.tellius.invoicemanagement.common.QueryParametersExtractor
import com.tellius.invoicemanagement.entities.writes.BusinessInvoicesJsonProtocol
import com.tellius.invoicemanagement.injection.Routable
import com.tellius.invoicemanagement.services.NotificationsService
import com.tellius.invoicemanagement.utils.ConstantUtils._
import com.typesafe.scalalogging.StrictLogging

class BusinessInvoiceController @Inject()(
  notifcationService: NotificationsService
) extends Routable
    with StrictLogging
    with QueryParametersExtractor
    with BusinessInvoicesJsonProtocol {
  override def getRoutes: Route = getBusinessNotifications

  private[this] val getBusinessNotifications: Route = get {
    path(VERSION_NUMBER / BUSINESS_INVOICE / JavaUUID) { businessUuid =>
      extract(_.request.uri.query()) { queryParametersString: Uri.Query =>
        logger.info(s"Get business invoices for ${businessUuid}")
        complete(
          notifcationService
            .getBusinessInvoices(
              businessUuid,
              convertQueryParamsToInvoiceQueryParams(queryParametersString)
            )
        )
      }
    }
  }
}
