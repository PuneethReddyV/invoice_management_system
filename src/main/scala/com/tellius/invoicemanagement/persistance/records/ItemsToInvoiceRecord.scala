package com.tellius.invoicemanagement.persistance.records

import java.util.UUID

case class ItemsToInvoiceRecord(id: Int,
                                invoiceUuid: UUID,
                                itemsUuid: UUID,
                                quantity: Int)
