package com.tellius.invoicemanagement.migration

object FlywayClient {

  def main(params: Array[String]): Unit =
    new FlywayClientImpl().main(Array("configure", "clean", "migrate"))
}

class FlywayClientImpl(flywayWrapper: FlywayWrapper = new FlywayWrapper()) {

  def main(params: Array[String]): Unit = {
    flywayWrapper.configure()
    params.map(executeCommand)
  }

  private def executeCommand(command: String): Any = {
    command match {
      case "migrate" => flywayWrapper.migrate()
      case "clean"   => flywayWrapper.clean()
      case "repair"  => flywayWrapper.repair()
      case _         =>
    }
  }

}
