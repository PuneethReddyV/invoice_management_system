package com.tellius.invoicemanagement.exception

trait DBException extends Exception

case class RecordNotFound(message: String) extends DBException

case class UpdateFailedException(message: String) extends DBException

case class CreateFailedException(message: String) extends DBException
