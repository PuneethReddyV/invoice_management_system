package com.tellius.invoicemanagement.entities.readers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tellius.invoicemanagement.utils.ConstantUtils.{NAME, PRICE}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class CreateNewItem(name: String, price: Double)

trait CreateNewItemEntityJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol {
  implicit lazy val createNewItemJsonFormat: RootJsonFormat[CreateNewItem] =
    jsonFormat(CreateNewItem.apply, NAME, PRICE)
}
