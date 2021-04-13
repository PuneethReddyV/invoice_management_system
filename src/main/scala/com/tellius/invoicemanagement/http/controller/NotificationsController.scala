package com.tellius.invoicemanagement.http.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.google.inject.Inject
import com.tellius.invoicemanagement.entities.readers.{
  UpdateNotifications,
  UpdateNotificationsEntityJsonProtocol
}
import com.tellius.invoicemanagement.injection.Routable
import com.tellius.invoicemanagement.services.NotificationsService
import com.tellius.invoicemanagement.utils.ConstantUtils._
import com.typesafe.scalalogging.StrictLogging

class NotificationsController @Inject()(
  notifcationService: NotificationsService
) extends Routable
    with StrictLogging
    with UpdateNotificationsEntityJsonProtocol {
  override def getRoutes: Route = update

  private[this] val update: Route = patch {
    path(VERSION_NUMBER / NOTIFICATION) {
      entity(as[UpdateNotifications]) { updateNotifications =>
        logger.info(
          s"Notifications PATCH for name = ${UpdateNotifications.toString}"
        )
        complete(notifcationService.updateNotifications(updateNotifications))
      }
    }
  }
}
