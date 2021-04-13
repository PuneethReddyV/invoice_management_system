package com.tellius.invoicemanagement.entities.writes

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tellius.invoicemanagement.utils.ConstantUtils._
import spray.json._

case class CustomersList(customerUuid: UUID,
                         customerName: String,
                         contactNumber: String,
                         emailId: String,
                         customerInvoicesList: Seq[InvoicesList])

trait CustomerListJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with ItemsJsonProtocol
    with InvoicesListJsonProtocol {
  implicit lazy val customersListJsonFormat: RootJsonFormat[CustomersList] =
    jsonFormat(
      CustomersList.apply,
      _UUID,
      NAME,
      CONTACT_NUMBER,
      EMAIL_ADDRESS,
      CUSTOMER_INVOICES
    )
}

case class BusinessInvoices(businessUuid: UUID = UUID.randomUUID(),
                            companyName: String = "",
                            customerInvoices: Seq[CustomersList] = Seq.empty)

trait BusinessInvoicesJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with CustomerListJsonProtocol {
  implicit lazy val businessInvoiceJsonFormat
    : RootJsonFormat[BusinessInvoices] =
    jsonFormat(BusinessInvoices.apply, _UUID, NAME, s"${CUSTOMER}s")
}
