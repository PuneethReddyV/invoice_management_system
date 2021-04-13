package com.tellius.invoicemanagement.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

import cats.implicits._
import com.tellius.invoicemanagement.common.EnumExtensions._
import com.tellius.invoicemanagement.error.ErrorDictionary.errorCodeToErrorsMap
import com.tellius.invoicemanagement.error.{
  BodyParamErrorCode,
  IMError,
  ValidationErrorCode
}
import com.tellius.invoicemanagement.exception.MalformedEntityException
import com.tellius.invoicemanagement.invoiceMngtParsingErrors.{
  InvoicetMngtParseError,
  ParsedInvoiceMnngtParseError
}
import com.typesafe.scalalogging.StrictLogging
import enumeratum.{Enum, EnumEntry}
import spray.json.{DefaultJsonProtocol, JsBoolean, JsNumber, JsString, JsValue}

import scala.util.{Failure, Success, Try}

trait InvoiceMngtParsingUtils extends StrictLogging with DefaultJsonProtocol {

  def readMandatoryField[T](parameters: Map[String, JsValue],
                            key: String,
                            missingError: IMError = errorCodeToErrorsMap(
                              ValidationErrorCode.InputParameterMissing
                            ))(
    parse: String => Try[T],
    parsingError: IMError
  ): ParsedInvoiceMnngtParseError[T] = {
    if (!parameters.contains(key)) {
      logger.error(s"Missing mandatory field $key, keys given ${parameters.keys
        .mkString("[", ", ", "]")}")
      InvoicetMngtParseError(key, missingError).invalidNel
    } else {
      parameters(key) match {
        case JsString(s) => validateParser(key, parse(s), parsingError)
        case JsNumber(n) => validateParser(key, parse(n.toString), parsingError)
        case JsBoolean(bool) =>
          validateParser(key, parse(bool.toString), parsingError)
        case _ =>
          InvoicetMngtParseError(
            key,
            errorCodeToErrorsMap(BodyParamErrorCode.MalformedJsonBody)
          ).invalidNel
      }
    }
  }

  def readDefaultField[T](parameters: Map[String, JsValue],
                          key: String,
                          default: T)(
    parse: String => Try[T],
    parsingError: IMError
  ): ParsedInvoiceMnngtParseError[T] =
    parameters.get(key) match {
      case None => default.validNel
      case Some(jsValue) =>
        jsValue match {
          case JsString(s) => validateParser(key, parse(s), parsingError)
          case JsNumber(n) =>
            validateParser(key, parse(n.toString), parsingError)
          case JsBoolean(bool) =>
            validateParser(key, parse(bool.toString), parsingError)
          case _ => InvoicetMngtParseError(key, parsingError).invalidNel
        }
    }

  def readOptionalField[T](fields: Map[String, JsValue],
                           fieldName: String,
                           skipStringMutation: Boolean = false)(
    parse: String => Try[T],
    parsingError: IMError
  ): ParsedInvoiceMnngtParseError[Option[T]] = {
    fields.get(fieldName) match {
      case None => None.validNel
      case Some(jsValue) =>
        jsValue match {
          case JsString(s) if skipStringMutation =>
            optionalValidateParser(fieldName, parse(s), parsingError)
          case JsString(s) =>
            optionalValidateParser(
              fieldName,
              parse(s.toLowerCase.trim),
              parsingError
            )
          case JsBoolean(bool) =>
            optionalValidateParser(
              fieldName,
              parse(bool.toString),
              parsingError
            )
          case JsNumber(n) =>
            optionalValidateParser(fieldName, parse(n.toString), parsingError)
          case _ => InvoicetMngtParseError(fieldName, parsingError).invalidNel
        }
    }
  }

  def validateWithRegex[T](pattern: String)(value: T): Try[T] = {
    import java.util.regex.Pattern

    if (Pattern
          .compile(pattern, Pattern.CASE_INSENSITIVE)
          .matcher(value.toString)
          .find()) {
      Try(value)
    } else {
      logger.error(s"$value didn't match with $pattern")
      throw new MalformedEntityException(
        s"$value didn't match with $pattern",
        Seq.empty
      )
    }
  }

  def noBusinessValidation[T](ele: T): Try[String] = Try(ele.toString)

  def extractUuid(uuidString: String): Try[UUID] =
    Try(UUID.fromString(uuidString))

  def extractDouble(doubleString: String): Try[Double] =
    Try(doubleString.toDouble)

  def extractLocalDate(dateString: String): Try[LocalDate] =
    Try(LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy")))

  def extractInt(intString: String): Try[Int] = Try(intString.toInt)

  private[this] def validateParser[T](
    key: String,
    mayBeError: Try[T],
    parsingError: IMError
  ): ParsedInvoiceMnngtParseError[T] =
    mayBeError match {
      case Success(result) =>
        result.validNel
      case _ =>
        InvoicetMngtParseError(key, parsingError).invalidNel
    }

  private[this] def optionalValidateParser[T](
    key: String,
    mayBeError: Try[T],
    parsingError: IMError
  ): ParsedInvoiceMnngtParseError[Option[T]] =
    mayBeError match {
      case Success(result) =>
        Some(result).validNel
      case _ =>
        InvoicetMngtParseError(key, parsingError).invalidNel
    }

  def readEnum[E <: EnumEntry](
    parameters: Map[String, JsValue],
    enum: Enum[E],
    key: String,
    missingError: IMError,
    parsingError: IMError
  ): ParsedInvoiceMnngtParseError[E] = {
    if (!parameters.contains(key)) {
      InvoicetMngtParseError(key, missingError).invalidNel
    } else {
      parameters(key) match {
        case JsString(jsValue) =>
          enum.withNameInsensitiveOption(jsValue) match {
            case Some(status) =>
              status.validNel
            case None => InvoicetMngtParseError(key, parsingError).invalidNel
          }
        case _ =>
          InvoicetMngtParseError(
            key,
            errorCodeToErrorsMap(BodyParamErrorCode.MalformedJsonBody)
          ).invalidNel
      }
    }
  }

  def readEnumWithDefault[E <: EnumEntry](
    parameters: Map[String, JsValue],
    enum: Enum[E],
    key: String,
    default: E,
    parsingError: IMError
  ): ParsedInvoiceMnngtParseError[E] = {
    if (!parameters.contains(key)) { default.validNel } else {
      parameters(key) match {
        case JsString(jsValue) =>
          enum.withNameLowercaseOnlyOption(jsValue) match {
            case Some(status) => status.validNel
            case None         => InvoicetMngtParseError(key, parsingError).invalidNel
          }
        case _ =>
          InvoicetMngtParseError(
            key,
            errorCodeToErrorsMap(BodyParamErrorCode.MalformedJsonBody)
          ).invalidNel
      }
    }
  }
}
