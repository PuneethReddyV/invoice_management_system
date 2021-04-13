package com.tellius.invoicemanagement.common

import com.typesafe.config.{Config, ConfigFactory}

class InvoiceMngtConfig(
  val root: Config = ConfigFactory.load(InvoiceConfigConstants.Filename.build)
) {

  object Http {
    private val httpConfig = root.getConfig("http")
    val interface: Option[String] = Some(httpConfig.getString("interface"))
    val port: Option[Int] = Some(httpConfig.getInt("port"))
  }

  object Db {
    private val dbConfig = root.getConfig("db")
    val url: String = dbConfig.getString("properties.url")
    val user: String = dbConfig.getString("properties.user")
    val password: String = dbConfig.getString("properties.password")
    val dbName: String = dbConfig.getString("dbName")
    val numThreads: Int = dbConfig.getInt("numThreads")
    val maxLifetime: Long = dbConfig.getInt("maxLifetime")
  }

  object Flyway {
    val locations: String = root.getConfig("flyway").getString("locations")
  }
}

class STMGTestConfig(
  root: Config = ConfigFactory.load(InvoiceConfigConstants.Filename.test)
) extends InvoiceMngtConfig(root) {

  val dbName: String = root.getString("db.dbName")

  object TestServer {
    private val testServerConfig = root.getConfig("testServer")

    object InvoiceManagement {
      private val invoiceManagementConfig =
        testServerConfig.getConfig("invoicemanagement")
      val host: String = invoiceManagementConfig.getString("host")
      val port: Int = invoiceManagementConfig.getInt("port")
    }
  }

  object WireMock {
    private val wireMockConfig = root.getConfig("wiremock")
    val port: Int = wireMockConfig.getInt("port")
  }
}
