package com.tellius.invoicemanagement.entities.readers

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tellius.invoicemanagement.entities.UUIDJsonProtocol
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import com.tellius.invoicemanagement.utils.ConstantUtils.{ITEM_UUID, QUANTITY}

case class InvoicedItems(itemUuid: UUID, quantity: Int)

trait InvoicedItemsJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with UUIDJsonProtocol {
  implicit lazy val createNewInvoicedItemJsonFormat
    : RootJsonFormat[InvoicedItems] =
    jsonFormat(InvoicedItems.apply, ITEM_UUID, QUANTITY)
}
