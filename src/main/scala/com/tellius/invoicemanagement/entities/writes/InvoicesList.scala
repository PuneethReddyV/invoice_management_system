package com.tellius.invoicemanagement.entities.writes

import java.time.LocalDate
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tellius.invoicemanagement.entities.eums.{
  InvoiceStatus,
  InvoiceStatusContextJsonProtocol
}
import com.tellius.invoicemanagement.utils.ConstantUtils._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class InvoicesList(invoiceUuid: UUID,
                        total: Double,
                        message: String,
                        paymentStatus: InvoiceStatus,
                        dueDate: Option[LocalDate],
                        items: Seq[Items])

trait InvoicesListJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with InvoiceStatusContextJsonProtocol
    with ItemsJsonProtocol
    with LocaLDateJsonProtocol {

  implicit lazy val invoicesListJsonFormat: RootJsonFormat[InvoicesList] =
    jsonFormat(
      InvoicesList.apply,
      _UUID,
      TOTAL,
      MESSAGE,
      PAYMENT_STATUS,
      DUE_DATE,
      ITEMS
    )
}
