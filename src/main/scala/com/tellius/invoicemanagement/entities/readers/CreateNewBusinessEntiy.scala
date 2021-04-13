package com.tellius.invoicemanagement.entities.readers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import com.tellius.invoicemanagement.utils.ConstantUtils.NAME

case class CreateNewBusinessEntiy(name: String)

trait CreateNewBusinessEntityJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol {
  implicit lazy val createNewBusinessUserJsonFormat
    : RootJsonFormat[CreateNewBusinessEntiy] =
    jsonFormat(CreateNewBusinessEntiy.apply, NAME)
}
