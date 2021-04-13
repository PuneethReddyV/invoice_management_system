package com.tellius.invoicemanagement.entities.eums

import com.tellius.invoicemanagement.common.EnumJsonProtocol
import enumeratum.EnumEntry.Snakecase
import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

sealed trait Due extends EnumEntry with Snakecase

object Due extends Enum[Due] {
  def values: immutable.IndexedSeq[Due] = findValues

  case object On extends Due
  case object WithIn extends Due
}

trait DueJsonProtocol {
  implicit lazy val dueFormat: EnumJsonProtocol[Due] = new EnumJsonProtocol(Due)
}

object DueContextJsonProtocol extends DueJsonProtocol
