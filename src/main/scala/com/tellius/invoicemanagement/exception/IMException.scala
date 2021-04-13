package com.tellius.invoicemanagement.exception

import com.tellius.invoicemanagement.error.IMError

abstract class IMException extends Exception {
  def message: String;
  def errors: Seq[IMError]
}

object IMValidationException {
  def apply(message: String, error: IMError): IMValidationException =
    IMValidationException(message, Seq(error))
}

case class IMValidationException(message: String, errors: Seq[IMError])
    extends IMException
case class IMGeneralException(message: String, errors: Seq[IMError])
    extends IMException
case class IMResourceNotFoundException(message: String, errors: Seq[IMError])
    extends IMException
case class ResourceAlreadyExistsException(message: String, errors: Seq[IMError])
    extends IMException
case class MalformedEntityException(message: String, errors: Seq[IMError])
    extends IMException
case class IMForbiddenException(message: String, errors: Seq[IMError] = Seq())
    extends IMException
case class IMMeds5xxException(message: String, errors: Seq[IMError])
    extends IMException

object MalformedEntityException {
  def apply(message: String, error: IMError): MalformedEntityException =
    MalformedEntityException(message, Seq(error))
}
