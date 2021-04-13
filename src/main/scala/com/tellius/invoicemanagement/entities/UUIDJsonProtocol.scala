package com.tellius.invoicemanagement.entities

import java.util.UUID

import akka.http.scaladsl.unmarshalling.{FromStringUnmarshaller, Unmarshaller}
import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

import scala.concurrent.Future

trait UUIDDeserializer {
  implicit def uuidMarshaller: FromStringUnmarshaller[UUID] =
    Unmarshaller(_ => s => Future.successful(UUID.fromString(s)))
}

trait UUIDJsonProtocol {
  implicit lazy val uuidFormat: RootJsonFormat[UUID] =
    new RootJsonFormat[UUID] {
      def write(uuid: UUID): JsString = JsString(uuid.toString)

      def read(value: JsValue): UUID = {
        value match {
          case JsString(uuid) => UUID.fromString(uuid)
          case _ =>
            throw DeserializationException(
              s"Expected hexadecimal UUID string but got '$value'"
            )
        }
      }
    }
}

object UUIDJsonProtocol extends UUIDJsonProtocol
