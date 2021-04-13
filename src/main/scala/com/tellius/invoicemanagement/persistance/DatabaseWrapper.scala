package com.tellius.invoicemanagement.persistance

import com.tellius.invoicemanagement.common.InvoiceConfigConstants
import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.JdbcBackend._

import scala.io.Source

class DatabaseWrapper(config: Config) {

  def this() = {
    this(ConfigFactory.load(InvoiceConfigConstants.Filename.build))
  }

  val database: Database =
    Database.forConfig("db", config)
}
