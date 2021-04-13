package com.tellius.invoicemanagement.entities.readers

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tellius.invoicemanagement.entities.UUIDJsonProtocol
import spray.json._
import com.tellius.invoicemanagement.utils.ConstantUtils._

case class UpdateNotifications(businessUuid: UUID,
                               customerUuid: UUID,
                               isEnabled: Boolean)

trait UpdateNotificationsEntityJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with UUIDJsonProtocol {
  implicit lazy val updateNotificationsJsonFormat
    : RootJsonFormat[UpdateNotifications] =
    jsonFormat(UpdateNotifications.apply, BUSINESS_UUID, CUSTOMER_UUID, ENABLED)
}
