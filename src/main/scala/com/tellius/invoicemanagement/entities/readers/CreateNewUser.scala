package com.tellius.invoicemanagement.entities.readers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import cats.implicits._
import com.tellius.invoicemanagement.common.EnumExtensions._
import com.tellius.invoicemanagement.common.{
  RequestBodyParser,
  RequestBodyParserSupport
}
import com.tellius.invoicemanagement.error.ErrorDictionary.errorCodeToErrorsMap
import com.tellius.invoicemanagement.error.ValidationErrorCode
import com.tellius.invoicemanagement.utils.ConstantUtils._
import com.tellius.invoicemanagement.utils.InvoiceMngtParsingUtils
import spray.json._

case class CreateNewUser(name: String, contactNumber: String, emailId: String)
    extends RequestBodyParser {

  def isValid: Boolean = true
  def errorMessage: String =
    "Error while parsing CreateNewUser."

  override def toString: String =
    s"user: $name, contact-number: $contactNumber, email: $emailId"

}

trait CreateNewUserJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol
    with InvoiceMngtParsingUtils
    with RequestBodyParserSupport {
  implicit lazy val createNewUserJsonProtocol: RootJsonFormat[CreateNewUser] =
    new RootJsonFormat[CreateNewUser] {

      override def read(json: JsValue): CreateNewUser = {
        val parameters: Map[String, JsValue] = json.asJsObject.fields
        val name = readMandatoryField[String](parameters, NAME)(
          noBusinessValidation,
          errorCodeToErrorsMap(ValidationErrorCode.InvalidInputParams)
        )
        val contactNumber =
          readMandatoryField[String](parameters, CONTACT_NUMBER)(
            validateWithRegex(
              """^(\+\d{1,2}\s?)?1?\-?\.?\s?\(?\d{3}\)?[\s.-]?\d{3}[\s.-]?\d{4}$"""
            ),
            errorCodeToErrorsMap(ValidationErrorCode.InvalidInputParams)
          )
        val emailId = readMandatoryField[String](parameters, EMAIL_ADDRESS)(
          validateWithRegex(
            "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}(.[a-z]{2,3})+$|^$"
          ),
          errorCodeToErrorsMap(ValidationErrorCode.InvalidInputParams)
        )
        val parseResult =
          (name, contactNumber, emailId).mapN(CreateNewUser.apply)
        handleInvoiceMngtErrors(parseResult)
      }

      override def write(obj: CreateNewUser): JsValue = JsObject(
        NAME -> JsString(obj.name),
        EMAIL_ADDRESS -> JsString(obj.emailId),
        CONTACT_NUMBER -> JsString(obj.contactNumber)
      )
    }
}
