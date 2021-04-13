package com.tellius.invoicemanagement

import cats.data.ValidatedNel

package object parsing {
  case class FieldParseError(fieldName: String)
  type ParsedValue[A] = ValidatedNel[FieldParseError, A]
}
