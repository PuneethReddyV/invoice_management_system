package com.tellius.invoicemanagement.persistance

import com.tellius.invoicemanagement.entities.eums.InvoiceStatus
import slick.jdbc.JdbcType

trait InvoiceManagementMySqlDriver extends CommonMySqlDriver {

  override val api: InvoiceManagementApi = new InvoiceManagementApi {}

  trait InvoiceManagementApi extends CommonApi {

    implicit val invoiceStatusMapper: JdbcType[InvoiceStatus] =
      enumeratumColumnMapper(InvoiceStatus)

  }
}

object InvoiceManagementMySqlDriver extends InvoiceManagementMySqlDriver
