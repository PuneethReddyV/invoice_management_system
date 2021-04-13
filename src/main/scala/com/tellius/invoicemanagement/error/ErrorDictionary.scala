package com.tellius.invoicemanagement.error

import enumeratum.EnumEntry

object ErrorDictionary {

  def errorCodeToErrorsMap: Map[String, IMError] = {
    val errors: Seq[(ErrorCode with EnumEntry, IMError)] = Seq(
      InternalErrorCode.DataBase404 -> IMError(
        "INVS-MGT-302",
        "Resource not found in Invoice Mngt System."
      ),
      InternalErrorCode.SystemError -> IMError(
        "INVS-MGT-1000",
        "Internal System error"
      ),
      //
      BodyParamErrorCode.InvalidBodyParams -> IMError(
        "INVS-MGT-105",
        "Request contains invalid value(s)."
      ),
      BodyParamErrorCode.MalformedJsonBody -> IMError(
        "INVS-MGT-106",
        "Malformed body parameter"
      ),
      QueryParamErrorCode.MalformedQueryParam -> IMError(
        "INVS-MGT-108",
        "Malformed query parameter."
      ),
      QueryParamErrorCode.MissingQueryParameter -> IMError(
        "INVS-MGT-109",
        "Missing query parameter."
      ),
      BodyParamErrorCode.EmptyBodyParams -> IMError(
        "INVS-MGT-110",
        "Invalid request. All parameters can not be empty."
      ),
      //
      ValidationErrorCode.InvalidInputParams -> IMError(
        "INVS-MGT-105",
        "Request contains invalid value(s)."
      ),
      ValidationErrorCode.InvalidUUIDformat -> IMError(
        "INVS-MGT-111",
        "Invalid URI/UUID format."
      ),
      ValidationErrorCode.ResourceAllReadyExists -> IMError(
        "INVS-MGT-200",
        "The resource already exists."
      )
    )

    errors
      .map { case (k, v) => (k.entryName, v) }
      .toMap
      .withDefaultValue(IMError("SM500", "System Error"))
  }

  def getErrorWithParams(error: IMError, parameters: String): IMError = {
    IMError(error.code, error.message, parameters)
  }
}
