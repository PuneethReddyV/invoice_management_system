package com.tellius.invoicemanagement.entities.eums

import com.tellius.invoicemanagement.common.EnumJsonProtocol
import enumeratum.EnumEntry.Snakecase
import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

sealed trait InvoiceStatus extends EnumEntry with Snakecase

object InvoiceStatus extends Enum[InvoiceStatus] {
  def values: immutable.IndexedSeq[InvoiceStatus] = findValues

  case object Paid extends InvoiceStatus
  case object UnPaid extends InvoiceStatus
}

trait InvoiceStatusContextJsonProtocol {

  implicit lazy val invoiceFormat: EnumJsonProtocol[InvoiceStatus] =
    new EnumJsonProtocol(InvoiceStatus)
}

object InvoiceStatusContextJsonProtocol extends InvoiceStatusContextJsonProtocol
