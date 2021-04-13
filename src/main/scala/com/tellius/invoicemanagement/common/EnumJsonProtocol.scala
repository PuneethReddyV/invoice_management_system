package com.tellius.invoicemanagement.common

import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}
import enumeratum._

class EnumJsonProtocol[E <: EnumEntry](enum: Enum[E])
    extends RootJsonFormat[E] {

  def read(json: JsValue): E = json match {
    case JsString(x) =>
      enum
        .withNameOption(x)
        .getOrElse(
          throw DeserializationException(
            s"$x is not a valid value from enum $enum"
          )
        )
    case _ =>
      throw DeserializationException(
        s"Expected a value from enum $enum instead of $json"
      )
  }

  def write(obj: E): JsValue = JsString(obj.entryName)
}
