package com.tellius.invoicemanagement.common

import cats.data.Validated.{Invalid, Valid}
import com.tellius.invoicemanagement.common.EnumExtensions._
import com.tellius.invoicemanagement.error.ErrorDictionary.{
  errorCodeToErrorsMap,
  getErrorWithParams
}
import com.tellius.invoicemanagement.error.{BodyParamErrorCode, IMError}
import com.tellius.invoicemanagement.exception.{
  IMValidationException,
  MalformedEntityException
}
import com.tellius.invoicemanagement.invoiceMngtParsingErrors.ParsedInvoiceMnngtParseError
import com.tellius.invoicemanagement.parsing.ParsedValue

trait RequestBodyParser {
  def isValid: Boolean

  def errorMessage: String
}

trait RequestBodyParserSupport {
  protected def handleErrors[T <: RequestBodyParser](
    parsedUpdate: ParsedValue[T]
  ): T = {
    parsedUpdate match {
      case Valid(update) =>
        if (!update.isValid) {
          throw MalformedEntityException(
            "Update payload is not valid",
            getErrorWithParams(
              errorCodeToErrorsMap(BodyParamErrorCode.EmptyBodyParams),
              update.errorMessage
            )
          )
        }
        update

      case Invalid(errors) =>
        val err = errorCodeToErrorsMap(BodyParamErrorCode.InvalidBodyParams)
        val parameters: String =
          errors.toList.map(_.fieldName).sorted.mkString(", ")
        throw MalformedEntityException(
          s"${err.message} Params: $parameters",
          getErrorWithParams(err, parameters)
        )
    }
  }

  protected def handleInvoiceMngtErrors[T <: RequestBodyParser](
    parsedUpdate: ParsedInvoiceMnngtParseError[T],
    isPatchRequest: Boolean = true
  ): T = {
    parsedUpdate match {
      case Valid(update) =>
        if (!update.isValid) {
          println(".invalid ." + update)
          throw MalformedEntityException(
            "Update payload is not valid",
            getErrorWithParams(
              errorCodeToErrorsMap(BodyParamErrorCode.InvalidBodyParams),
              update.errorMessage
            )
          )
        }
        update

      case Invalid(errors) =>
        println(".." + errors)
        val uniqueErrors = errors.toList.groupBy(_.invoiceMngtError.code).map {
          case (errorCode, groupedErrors) =>
            val fields = groupedErrors.map(_.fieldName).mkString(", ")
            val errorMessage =
              groupedErrors.headOption.fold("")(_.invoiceMngtError.message)
            IMError(errorCode, errorMessage, fields)
        }

        if (isPatchRequest) {
          throw MalformedEntityException(
            s"Some errors with ${errors.toList.map(_.fieldName).mkString(", ")}",
            uniqueErrors.toSeq
          )
        } else {
          throw IMValidationException(
            s"Some errors with ${errors.toList.map(_.fieldName).mkString(", ")}",
            uniqueErrors.toSeq
          )
        }
    }
  }
}
