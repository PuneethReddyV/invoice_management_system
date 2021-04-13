package com.tellius.invoicemanagement.http.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.google.inject.Inject
import com.tellius.invoicemanagement.entities.readers.{
  CreateNewItem,
  CreateNewItemEntityJsonProtocol
}
import com.tellius.invoicemanagement.entities.writes.CreatedResourceResponseJsonProtocol
import com.tellius.invoicemanagement.injection.Routable
import com.tellius.invoicemanagement.services.ItemService
import com.tellius.invoicemanagement.utils.ConstantUtils._
import com.typesafe.scalalogging.StrictLogging

class ItemController @Inject()(itemService: ItemService)
    extends Routable
    with StrictLogging
    with CreateNewItemEntityJsonProtocol
    with CreatedResourceResponseJsonProtocol {
  override def getRoutes: Route = createUserEntity

  private[this] val createUserEntity: Route = post {
    path(VERSION_NUMBER / ITEM) {
      entity(as[CreateNewItem]) { newItem =>
        logger.info(s"User POST for name = ${newItem.toString}")
        complete(itemService.createNewItemResource(newItem))
      }
    }
  }
}
