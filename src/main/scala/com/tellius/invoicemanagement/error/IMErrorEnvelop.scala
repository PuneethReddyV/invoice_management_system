package com.tellius.invoicemanagement.error

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

case class IMError(code: String, message: String, data: String = "")

case class IMErrorEnvelop(errors: Seq[IMError]) {
  override def toString: String =
    s"Errors: " +
      errors
        .map(error => "ErrorCode: " + error.code + " Message: " + error.message)
        .mkString
}

object IMErrorMessageJsonProtocol
    extends SprayJsonSupport
    with DefaultJsonProtocol {

  implicit object IMErrorFormat extends RootJsonFormat[IMError] {
    override def read(json: JsValue): IMError =
      throw new UnsupportedOperationException("Attempt to deserialize IMError")

    override def write(obj: IMError): JsValue = JsObject(
      "message_id" -> JsString(obj.code),
      "message" -> JsString(obj.message),
      "data" -> JsString(obj.data)
    )
  }

  implicit object IMErrorEnvelopFormat extends RootJsonFormat[IMErrorEnvelop] {
    override def read(json: JsValue): IMErrorEnvelop =
      throw new UnsupportedOperationException(
        "Attempt to deserialize IMErrorMessageEnvelope"
      )

    override def write(obj: IMErrorEnvelop): JsValue = JsObject(
      "errors" -> JsArray(obj.errors.map(IMErrorFormat.write): _*)
    )
  }
}
