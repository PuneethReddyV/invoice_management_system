package com.tellius.invoicemanagement.entities.readers

import java.time.LocalDate
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tellius.invoicemanagement.common.{
  RequestBodyParser,
  RequestBodyParserSupport
}
import com.tellius.invoicemanagement.error.ErrorDictionary.errorCodeToErrorsMap
import com.tellius.invoicemanagement.error.{
  BodyParamErrorCode,
  ValidationErrorCode
}
import com.tellius.invoicemanagement.utils.ConstantUtils._
import com.tellius.invoicemanagement.utils.InvoiceMngtParsingUtils
import spray.json._
import com.tellius.invoicemanagement.common.EnumExtensions._
import cats.implicits._
import com.tellius.invoicemanagement.entities.eums.InvoiceStatus
import com.tellius.invoicemanagement.invoiceMngtParsingErrors.{
  InvoicetMngtParseError,
  ParsedInvoiceMnngtParseError
}

import scala.util.{Success, Try}

case class CreateNewInvoice(businessUuid: UUID,
                            customerUuid: UUID,
                            amount: Double,
                            paymentStatus: InvoiceStatus,
                            dueDate: Option[LocalDate],
                            items: Seq[InvoicedItems])
    extends RequestBodyParser {

  def isValid: Boolean = true

  def errorMessage: String = ""
}

trait CreateNewInvoiceEntityJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with RequestBodyParserSupport
    with InvoiceMngtParsingUtils
    with InvoicedItemsJsonProtocol {
  implicit lazy val createNewInvoiceJsonFormat
    : RootJsonFormat[CreateNewInvoice] = new RootJsonFormat[CreateNewInvoice] {
    //writes is no where used
    override def write(obj: CreateNewInvoice): JsValue = JsObject.empty

    override def read(json: JsValue): CreateNewInvoice = {
      val parameters: Map[String, JsValue] = json.asJsObject.fields
      val businessUuid = readMandatoryField[UUID](parameters, BUSINESS_UUID)(
        extractUuid,
        errorCodeToErrorsMap(ValidationErrorCode.InvalidInputParams)
      )

      val customerUuid = readMandatoryField[UUID](parameters, CUSTOMER_UUID)(
        extractUuid,
        errorCodeToErrorsMap(ValidationErrorCode.InvalidInputParams)
      )

      val amount = readMandatoryField[Double](parameters, AMOUNT)(
        extractDouble,
        errorCodeToErrorsMap(ValidationErrorCode.InvalidInputParams)
      )

      val paymentStatus = readEnum[InvoiceStatus](
        parameters,
        InvoiceStatus,
        PAYMENT_STATUS,
        errorCodeToErrorsMap(ValidationErrorCode.InputParameterMissing),
        errorCodeToErrorsMap(BodyParamErrorCode.InvalidBodyParams)
      )
      val dueDate = readOptionalField[LocalDate](
        parameters,
        DUE_DATE,
        skipStringMutation = true
      )(
        extractLocalDate,
        errorCodeToErrorsMap(ValidationErrorCode.InvalidInputParams)
      )
      println("due date ")
      val parseResult =
        (
          businessUuid,
          customerUuid,
          amount,
          paymentStatus,
          dueDate,
          readInvoiceItems(parameters)
        ).mapN(CreateNewInvoice.apply)
      println(handleInvoiceMngtErrors(parseResult))
      handleInvoiceMngtErrors(parseResult)
    }

  }

  private def readInvoiceItems(
    fields: Map[String, JsValue]
  ): ParsedInvoiceMnngtParseError[Seq[InvoicedItems]] =
    Try(fields(ITEMS).toJson.convertTo[Seq[InvoicedItems]]) match {
      case Success(result) => result.validNel
      case _ =>
        InvoicetMngtParseError(
          ITEMS,
          errorCodeToErrorsMap(ValidationErrorCode.InvalidInputParams)
        ).invalidNel
    }

}
