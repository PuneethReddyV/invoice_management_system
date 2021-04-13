package com.tellius.invoicemanagement.error

import enumeratum.EnumEntry.{Camelcase, Snakecase}
import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable
import scala.language.implicitConversions

trait ErrorCode

sealed trait InternalErrorCode extends ErrorCode with EnumEntry with Camelcase

object InternalErrorCode extends Enum[InternalErrorCode] {
  val values: immutable.IndexedSeq[InternalErrorCode] = findValues

  case object DataBase404 extends InternalErrorCode
  case object SystemError extends InternalErrorCode

}

sealed trait ValidationErrorCode extends ErrorCode with EnumEntry with Camelcase
// validation error codes for study_management
object ValidationErrorCode extends Enum[ValidationErrorCode] {
  val values: immutable.IndexedSeq[ValidationErrorCode] = findValues

  case object InvalidUUIDformat extends ValidationErrorCode
  case object InvalidInputParams extends ValidationErrorCode
  case object InputParameterMissing extends ValidationErrorCode
  case object ResourceAllReadyExists extends ValidationErrorCode
}

sealed trait QueryParamErrorCode extends ErrorCode with EnumEntry with Snakecase

object QueryParamErrorCode extends Enum[QueryParamErrorCode] {
  val values: immutable.IndexedSeq[QueryParamErrorCode] = findValues

  case object MalformedQueryParam extends QueryParamErrorCode
  case object MissingQueryParameter extends QueryParamErrorCode
}

sealed trait BodyParamErrorCode extends ErrorCode with EnumEntry with Camelcase

object BodyParamErrorCode extends Enum[BodyParamErrorCode] {
  val values: immutable.IndexedSeq[BodyParamErrorCode] = findValues

  case object EmptyBodyParams extends BodyParamErrorCode
  case object InvalidBodyParams extends BodyParamErrorCode
  case object MalformedJsonBody extends BodyParamErrorCode
}
