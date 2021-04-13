package com.tellius.invoicemanagement.persistance.records

import java.time.LocalDate
import java.util.UUID

import com.tellius.invoicemanagement.entities.eums.InvoiceStatus

case class InvoiceRecord(uuid: UUID,
                         businessUuid: UUID,
                         customerUUID: UUID,
                         amount: Double,
                         paymentStatus: InvoiceStatus,
                         dueDate: Option[LocalDate],
                         isDeleted: Boolean)
