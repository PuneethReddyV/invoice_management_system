package com.tellius.invoicemanagement.persistance.records

import java.util.UUID

case class BusinessToCustomerRecord(id: Int,
                                    businessUuid: UUID,
                                    customerUuid: UUID,
                                    remainderEnabled: Boolean)
