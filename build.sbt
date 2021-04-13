import BuildSettings._
import sbt.Keys._
import sbt._

import scala.language.postfixOps

conflictManager := ConflictManager.latestRevision

lazy val studyManagement = project
  .in(file("."))
  .configs(IntegrationTest)
  .settings(
    basicSettings,
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
    dependencyOverrides ++= Dependencies.dependencyOverrides,
    libraryDependencies ++=
      Dependencies.compileDeps ++
        Dependencies.testDeps
  )

addCommandAlias("compileAll", ";compile;test:compile;it:compile")

compile in Compile := ((compile in Compile)).value

javaOptions ++= Seq(
  "-Dfile.encoding=UTF-8",
  "-J-Xms1G",
  "-J-Xmx4G",
  "-J-XX:+UseG1GC",
  "-Dscala.concurrent.context.numThreads=300",
  "-Dscala.concurrent.context.maxThreads=300"
)
