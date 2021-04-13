package com.tellius.invoicemanagement.entities.readers

import com.tellius.invoicemanagement.common.RequestBodyParser
import com.tellius.invoicemanagement.entities.eums.{Due, InvoiceStatus}

case class InvoiceQueryParameters(due: Due,
                                  interval: Int,
                                  paymentStatus: InvoiceStatus =
                                    InvoiceStatus.UnPaid)
    extends RequestBodyParser {

  def isValid: Boolean = true

  def errorMessage: String = ""
}
