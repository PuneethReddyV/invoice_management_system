import sbt.Keys._
import sbt.URL

object BuildSettings {
  val VERSION = "2021.1.0.x"

  lazy val basicSettings = Seq(
    version := VERSION,
    name := "InvoiceManagementSystem",
    homepage := Some(
      new URL("http://github.com/PuneethReddyV/invoice_management")
    ),
    organization := "myself",
    organizationHomepage := Some(new URL("http://github.com/PuneethReddyV/")),
    description := "Invoice Management System",
    scalaVersion := "2.13.5",
    resolvers ++= Dependencies.resolutionRepos,
    javacOptions := Seq("-source", "1.11", "-target", "1.11"),
    scalacOptions := Seq(
      "-encoding",
      "utf8",
      "-feature",
      "-unchecked",
      "-deprecation",
      "-target:jvm-1.8",
      "-language:_",
      "-Xlint:adapted-args",
      "-Werror"
    )
  )
}
