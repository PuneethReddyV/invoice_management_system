package com.tellius.invoicemanagement

import com.tellius.invoicemanagement.error.IMError
import cats.data.ValidatedNel

//This package object is different from parsing object. It have additional information like field name and SMError.
package object invoiceMngtParsingErrors {
  case class InvoicetMngtParseError(fieldName: String,
                                    invoiceMngtError: IMError)
  type ParsedInvoiceMnngtParseError[A] = ValidatedNel[InvoicetMngtParseError, A]
}
