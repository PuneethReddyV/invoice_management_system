package com.tellius.invoicemanagement.common

import akka.http.scaladsl.model.Uri
import com.tellius.invoicemanagement.entities.eums.{Due, InvoiceStatus}
import com.tellius.invoicemanagement.invoiceMngtParsingErrors.ParsedInvoiceMnngtParseError
import com.tellius.invoicemanagement.utils.InvoiceMngtParsingUtils
import spray.json._
import com.tellius.invoicemanagement.common.EnumExtensions._
import com.tellius.invoicemanagement.error.ErrorDictionary.errorCodeToErrorsMap
import com.tellius.invoicemanagement.error.{
  QueryParamErrorCode,
  ValidationErrorCode
}
import com.tellius.invoicemanagement.utils.ConstantUtils._
import cats.implicits._
import com.tellius.invoicemanagement.entities.readers.InvoiceQueryParameters

trait QueryParametersExtractor
    extends RequestBodyParserSupport
    with InvoiceMngtParsingUtils {

  def extractQueryParams(queryString: String): InvoiceQueryParameters = {
    val keyValuePairs = queryString.parseJson.asJsObject.fields
    val interval: ParsedInvoiceMnngtParseError[Int] = readMandatoryField[Int](
      keyValuePairs,
      INTERVAL,
      errorCodeToErrorsMap(QueryParamErrorCode.MissingQueryParameter)
    )(extractInt, errorCodeToErrorsMap(ValidationErrorCode.InvalidInputParams))
    val paymentStatus = readEnumWithDefault[InvoiceStatus](
      keyValuePairs,
      InvoiceStatus,
      PAYMENT_STATUS,
      InvoiceStatus.UnPaid,
      errorCodeToErrorsMap(ValidationErrorCode.InvalidInputParams)
    )
    val due = readEnum[Due](
      keyValuePairs,
      Due,
      DUE,
      errorCodeToErrorsMap(QueryParamErrorCode.MissingQueryParameter),
      errorCodeToErrorsMap(ValidationErrorCode.InvalidInputParams)
    )
    val parseResult =
      (due, interval, paymentStatus).mapN(InvoiceQueryParameters.apply)
    handleInvoiceMngtErrors(parseResult, isPatchRequest = false)
  }

  def convertQueryParamsToInvoiceQueryParams(
    queryParametersString: Uri.Query
  ): InvoiceQueryParameters = {
    extractQueryParams(
      queryParametersString
        .map {
          case (key: String, value: String) =>
            s""""$key": "${value.replace("\"", "")}""""
        }
        .mkString("{", ",", "}")
    )
  }
}
