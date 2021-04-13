package com.tellius.invoicemanagement.common

import enumeratum.values.IntEnumEntry
import enumeratum.{Enum, EnumEntry}

import scala.language.implicitConversions

object EnumExtensions {
  implicit class StringEnumeratumOps(s: String) {
    def toEnum[E <: EnumEntry](enum: Enum[E]): E = enum.withName(s)

    def toEnumOpt[E <: EnumEntry](enum: Enum[E]): Option[E] =
      enum.withNameOption(s)
  }

  implicit class EnumeratumOps[E <: EnumEntry](enum: Enum[E]) {
    def fromString(s: String): E = enum.withName(s)

    def fromOptString(maybeString: Option[String]): Option[E] =
      maybeString.flatMap(enum.withNameOption)
  }

  implicit def enumOrdering[E <: EnumEntry]: Ordering[E] =
    (x: E, y: E) => x.entryName.compareTo(y.entryName)

  implicit def enumEntryToString[E <: EnumEntry](entry: E): String =
    entry.entryName

  implicit def enumEntryToStringOption[E <: EnumEntry](
    entry: Option[E]
  ): Option[String] = entry.map(_.entryName)

  implicit def enumEntryToInt[E <: IntEnumEntry](entry: E): Int = entry.value
}
