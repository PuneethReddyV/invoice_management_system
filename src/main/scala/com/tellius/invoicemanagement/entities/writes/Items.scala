package com.tellius.invoicemanagement.entities.writes

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tellius.invoicemanagement.entities.UUIDJsonProtocol
import com.tellius.invoicemanagement.utils.ConstantUtils.{
  NAME,
  PRICE,
  QUANTITY,
  _UUID
}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Items(uuid: UUID, name: String, price: Double, quantity: Int)

trait ItemsJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with UUIDJsonProtocol {
  implicit lazy val itemsJsonFormat: RootJsonFormat[Items] =
    jsonFormat(Items.apply, _UUID, NAME, PRICE, QUANTITY)
}
