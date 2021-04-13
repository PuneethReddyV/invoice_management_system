package com.tellius.invoicemanagement.migration

import com.tellius.invoicemanagement.common.InvoiceMngtConfig
import com.typesafe.scalalogging.StrictLogging
import org.flywaydb.core.Flyway

class FlywayWrapper(flyway: Flyway = new Flyway(),
                    invoiceConfig: InvoiceMngtConfig = new InvoiceMngtConfig())
    extends StrictLogging {

  def configure(): Any = {
    logger.info("Start - Flyway configuration")
    flyway.setDataSource(
      invoiceConfig.Db.url,
      invoiceConfig.Db.user,
      invoiceConfig.Db.password
    )
    flyway.setSchemas(invoiceConfig.Db.dbName)
    flyway.setLocations(invoiceConfig.Flyway.locations)
    logger.info("End - Flyway configuration")
  }

  def migrate(): Any = {
    logger.info("Start - Flyway migration")
    flyway.migrate()
    logger.info("End - Flyway migration")
  }

  def clean(): Any = {
    logger.info("Start - Flyway clean")
    flyway.clean()
    logger.info("End - Flyway clean")
  }

  def repair(): Any = {
    logger.info("Start - Flyway repair")
    flyway.repair()
    logger.info("End - Flyway repair")
  }
}
