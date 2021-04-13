package com.tellius.invoicemanagement.persistance.records

import java.util.UUID

case class CustomerRecord(uuid: UUID,
                          name: String,
                          contactNumber: String,
                          emailId: String)
