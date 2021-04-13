import sbt.{ModuleID, _}

object Dependencies {

  val resolutionRepos = Seq(
    Resolver.mavenLocal,
    Resolver.sonatypeRepo("snapshots"),
    "Flyway" at "https://flywaydb.org/repo",
    "jitpack" at "https://jitpack.io",
    Resolver.bintrayRepo("streetcontxt", "maven")
  )

  def provided(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  lazy val compileDeps: Seq[ModuleID] = Seq(
    akkaHttpSprayJson,
    flyWayCore,
    logbackClassic,
    scalaGuice,
    scalaLogging,
    mySql,
    slick,
    slickHikaricp,
    cats,
    enumeratum,
    kplScala,
    akkaStream,
  )

  lazy val testDeps: Seq[ModuleID] = Seq(
    // akkaHttpTestKit,
    jUnitInterface,
    scalaMock,
    sprayJson,
    wireMock,
    scalaxml,
    mySql,
    easyMock,
  ) map (_ % "test")

  private object Version {
    val akkaHttp: String = "10.1.12"
    val flyWay: String = "5.0.7"
    val logBack: String = "1.2.3"
    val scalaLog: String = "3.9.2"
    val guice: String = "5.0.0"
    val wiremock: String = "2.25.1"
    val guava: String = "23.0"
    val scalaXml: String = "1.2.0"
    val sprayJson: String = "1.3.6"
    val mysql: String = "5.1.49"
    val slick: String = "3.3.3"
    val akka = "2.6.5"
  }

  val akkaHttp: ModuleID = "com.typesafe.akka" %% "akka-http" % Version.akkaHttp
  val akkaHttpSprayJson
    : ModuleID = "com.typesafe.akka" %% "akka-http-spray-json" % Version.akkaHttp
  val akkaStream: ModuleID = "com.typesafe.akka" %% "akka-stream" % Version.akka
  val akkaTestkit
    : ModuleID = "com.typesafe.akka" %% "akka-testkit" % Version.akka

  val flyWayCore: ModuleID = "org.flywaydb" % "flyway-core" % Version.flyWay
  val guava: ModuleID = "com.google.guava" % "guava" % Version.guava
  val logbackClassic
    : ModuleID = "ch.qos.logback" % "logback-classic" % Version.logBack

  val slick
    : ModuleID = "com.typesafe.slick" %% "slick" % Version.slick excludeAll
    ExclusionRule("org.reactivestreams", "reactive-streams")

  val slickHikaricp
    : ModuleID = "com.typesafe.slick" %% "slick-hikaricp" % Version.slick

  val scalaGuice
    : ModuleID = "net.codingwell" %% "scala-guice" % Version.guice excludeAll (
    ExclusionRule(organization = "org.scala-lang"),
    ExclusionRule(organization = "com.google.guava")
  )

  val scalaLogging
    : ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % Version.scalaLog

  val sprayJson: ModuleID = "io.spray" %% "spray-json" % Version.sprayJson
  val mySql: ModuleID = "mysql" % "mysql-connector-java" % Version.mysql
  val cats: ModuleID = "org.typelevel" %% "cats-core" % "2.4.2"

  val enumeratum: ModuleID = "com.beachape" %% "enumeratum" % "1.6.1"
  val kplScala: ModuleID = "com.streetcontxt" %% "kpl-scala" % "1.1.0"

  val scalaxml
    : ModuleID = "org.scala-lang.modules" %% "scala-xml" % Version.scalaXml
  val akkaHttpTestKit
    : ModuleID = "com.typesafe.akka" % "akka-http-testkit" % Version.akkaHttp
  val jUnitInterface
    : ModuleID = "com.novocode" % "junit-interface" % "0.11" excludeAll (
    ExclusionRule("org.hamcrest", "hamcrest-core"),
    ExclusionRule("junit", "junit")
  )

  val scalaMock: ModuleID = "org.scalamock" %% "scalamock" % "5.1.0"

  val wireMock
    : ModuleID = "com.github.tomakehurst" % "wiremock-jre8" % Version.wiremock
  val easyMock = "org.easymock" % "easymock" % "3.6"

  // Overrides
  val dependencyOverrides = Seq(
    "com.fasterxml.jackson.core" % "jackson-annotations" % "2.12.2",
    "com.fasterxml.jackson.core" % "jackson-core" % "2.12.2",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.12.2",
    "com.google.code.findbugs" % "jsr305" % "3.0.1",
    "com.google.guava" % "guava" % "23.0",
    "com.typesafe" % "config" % "1.3.2",
    "org.ow2.asm" % "asm" % "5.0.4",
    "org.scala-lang" % "scala-library" % "2.13.4",
    "org.scala-lang" % "scala-reflect" % "2.13.4",
    "org.scala-lang.modules" %% "scala-xml" % Version.scalaXml,
    "org.scala-lang.modules" %% "scala-collection-compat" % "2.3.2",
    "com.typesafe.akka" %% "akka-http" % Version.akkaHttp,
    "com.typesafe.akka" %% "akka-http-spray-json" % Version.akkaHttp,
    "com.typesafe.akka" %% "akka-stream" % Version.akka,
    "com.typesafe.akka" %% "akka-actor" % Version.akka,
    "com.typesafe.akka" %% "akka-slf4j" % Version.akka,
    sprayJson,
    scalaLogging,
    "commons-codec" % "commons-codec" % "1.15",
    "com.github.cb372" %% "scalacache-core" % "0.28.0",
    "com.beachape" %% "enumeratum" % "1.6.1",
    "com.github.everit-org" % "json-schema" % "1.12.2"
  )
}
