package com.tellius.invoicemanagement.error

import akka.http.scaladsl.model.StatusCodes.UnprocessableEntity
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server._
import com.tellius.invoicemanagement.common.EnumExtensions._
import com.tellius.invoicemanagement.error.ErrorDictionary._
import com.tellius.invoicemanagement.error.IMErrorMessageJsonProtocol._
import com.typesafe.scalalogging.StrictLogging
import spray.json.DeserializationException
import spray.json.JsonParser.ParsingException

object IMRejectionHandler extends StrictLogging {

  val rejectionHandler: RejectionHandler = RejectionHandler
    .newBuilder()
    .handle {

      case InvalidRequiredValueForQueryParamRejection(
          parameterName,
          excepted,
          provided
          ) =>
        logger.error(
          s"$parameterName: allowed values are $excepted. Given value $provided, is invalid."
        )
        complete(
          UnprocessableEntity -> IMErrorEnvelop(
            getErrorWithParams(
              errorCodeToErrorsMap(ValidationErrorCode.InvalidInputParams),
              parameterName
            ) :: Nil
          )
        )

      case ValidationRejection(msg, ex) =>
        logger.error(msg, ex)
        complete(
          UnprocessableEntity -> IMErrorEnvelop(
            errorCodeToErrorsMap(InternalErrorCode.SystemError) :: Nil
          )
        )

      case MalformedRequestContentRejection(
          msg,
          error: DeserializationException
          ) =>
        logger.error(msg, error)
        complete(
          UnprocessableEntity -> IMErrorEnvelop(
            errorCodeToErrorsMap(InternalErrorCode.SystemError) :: Nil
          )
        )

      case MalformedRequestContentRejection(message, error: ParsingException) =>
        logger.error(message, error.detail)
        complete(
          UnprocessableEntity ->
            IMErrorEnvelop(
              errorCodeToErrorsMap(BodyParamErrorCode.MalformedJsonBody) :: Nil
            )
        )

      case MissingQueryParamRejection(parameterName) =>
        val message = s"Missing query parameter: '$parameterName'"
        logger.error(message)
        complete(
          UnprocessableEntity -> IMErrorEnvelop(
            getErrorWithParams(
              errorCodeToErrorsMap(QueryParamErrorCode.MissingQueryParameter),
              parameterName
            ) :: Nil
          )
        )

      case MissingHeaderRejection(headerName) =>
        val message = s"Missing header: '$headerName'"
        logger.error(message)
        complete(
          UnprocessableEntity -> IMErrorEnvelop(
            errorCodeToErrorsMap(InternalErrorCode.SystemError) :: Nil
          )
        )

      case MalformedQueryParamRejection(parameterName, errorMsg, _) =>
        val message = s"Unable to parse value of parameter '$parameterName'"
        logger.error(message, errorMsg)
        complete(
          UnprocessableEntity -> IMErrorEnvelop(
            getErrorWithParams(
              errorCodeToErrorsMap(QueryParamErrorCode.MalformedQueryParam),
              parameterName
            ) :: Nil
          )
        )
    }
    .result()

}
