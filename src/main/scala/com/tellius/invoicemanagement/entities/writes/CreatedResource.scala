package com.tellius.invoicemanagement.entities.writes

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tellius.invoicemanagement.entities.UUIDJsonProtocol
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import com.tellius.invoicemanagement.utils.ConstantUtils._UUID

case class CreatedResource(uuid: UUID)

trait CreatedResourceResponseJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with UUIDJsonProtocol {

  implicit lazy val createdClinicalTrialRegistrationFormat
    : RootJsonFormat[CreatedResource] =
    jsonFormat(CreatedResource, _UUID)
}
