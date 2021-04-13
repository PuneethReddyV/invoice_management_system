package com.tellius.invoicemanagement.exception

import akka.http.scaladsl.model.StatusCodes.{InternalServerError, NotFound}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.ExceptionHandler
import com.tellius.invoicemanagement.error.InternalErrorCode.DataBase404
import com.tellius.invoicemanagement.error.{
  ErrorDictionary,
  IMErrorEnvelop,
  InternalErrorCode
}
import com.typesafe.scalalogging.StrictLogging
import com.tellius.invoicemanagement.common.EnumExtensions._

object IMExceptionHandler extends StrictLogging {
  import com.tellius.invoicemanagement.error.IMErrorMessageJsonProtocol._

  val exceptionHandler: ExceptionHandler = ExceptionHandler {

    case e: RecordNotFound => {
      logger.error(
        "Invoice management db missing record exception: " + e.message
      )
      complete(
        NotFound -> IMErrorEnvelop(
          ErrorDictionary.errorCodeToErrorsMap(DataBase404) :: Nil
        )
      )
    }

    case e: RuntimeException =>
      logger.error("Invoice management general exception: ", e)
      complete(
        InternalServerError -> IMErrorEnvelop(
          ErrorDictionary
            .errorCodeToErrorsMap(InternalErrorCode.SystemError) :: Nil
        )
      )

    case e: Exception =>
      logger.error("Invoice management general exception: ", e)
      complete(
        InternalServerError -> IMErrorEnvelop(
          ErrorDictionary
            .errorCodeToErrorsMap(InternalErrorCode.SystemError) :: Nil
        )
      )
  }

}
