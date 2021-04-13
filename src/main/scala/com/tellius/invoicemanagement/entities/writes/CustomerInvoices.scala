package com.tellius.invoicemanagement.entities.writes

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tellius.invoicemanagement.utils.ConstantUtils._
import spray.json._

case class BusinessList(businessUuid: UUID,
                        name: String,
                        businessInvoicesList: Seq[InvoicesList])

trait BusinessInvoicesListJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with InvoicesListJsonProtocol
    with LocaLDateJsonProtocol {
  implicit lazy val businessInvoicesListJsonFormat
    : RootJsonFormat[BusinessList] =
    jsonFormat(BusinessList.apply, _UUID, NAME, BUSINESS_INVOICES)
}

case class CustomerInvoices(customerUuid: UUID = UUID.randomUUID(),
                            customerName: String = "",
                            contactNumber: String = "",
                            emailId: String = "",
                            businessList: Seq[BusinessList] = Seq.empty)

trait CustomerInvoicesJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with BusinessInvoicesListJsonProtocol {
  implicit lazy val businessInvoiceJsonFormat
    : RootJsonFormat[CustomerInvoices] =
    jsonFormat(
      CustomerInvoices.apply,
      _UUID,
      NAME,
      CONTACT_NUMBER,
      EMAIL_ADDRESS,
      s"${BUSINESS}s"
    )
}
