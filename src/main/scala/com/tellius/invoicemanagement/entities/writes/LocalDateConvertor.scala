package com.tellius.invoicemanagement.entities.writes

import java.time.LocalDate

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tellius.invoicemanagement.utils.ConstantUtils.DUE_DATE
import spray.json._

trait LocaLDateJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit lazy val localDateJsonFormat: RootJsonFormat[LocalDate] =
    new RootJsonFormat[LocalDate] {

      override def read(json: JsValue): LocalDate =
        LocalDate.parse(json.toString)

      override def write(obj: LocalDate): JsValue = JsString(obj.toString)
    }
}
