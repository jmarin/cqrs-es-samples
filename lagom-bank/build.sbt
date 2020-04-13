import com.lightbend.lagom.core.LagomVersion

organization in ThisBuild := "com.github.jmarin"

version in ThisBuild := "1.0.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.11"

val AkkaVersion = "2.6.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.14.1" % Test
val akkaDiscovery = "com.typesafe.akka" %% "akka-discovery" % AkkaVersion
val akkaKubernetes = "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % "1.0.5"
val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.1.11"
val akkaStream = "com.typesafe.akka" %% "akka-stream" % AkkaVersion
val h2 = "com.h2database" % "h2" % "1.4.196"
val postgres = "org.postgresql" % "postgresql" % "42.2.8"

lazy val `lagom-bank` = (project in file("."))
  .aggregate(
    `lagom-bank-api`,
    `lagom-bank-impl`
  )

lazy val `lagom-bank-api` = (project in file("lagom-bank-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-bank-impl` = (project in file("lagom-bank-impl"))
  .enablePlugins(LagomScala, Cinnamon)
  .settings(
    cinnamon in run := true,
    cinnamon in test := false,
    libraryDependencies ++= Seq(
      lagomScaladslAkkaDiscovery,
      akkaKubernetes,
      lagomScaladslKafkaBroker,
      lagomScaladslPersistenceCassandra,
      lagomScaladslPersistenceJdbc,
      lagomScaladslTestKit,
      lagomScaladslApi,
      macwire,
      scalaTest,
      scalaCheck,
      h2,
      postgres,
      // Use Coda Hale Metrics and Lagom instrumentation
      Cinnamon.library.cinnamonCHMetrics3,
      Cinnamon.library.cinnamonLagom,
      Cinnamon.library.cinnamonOpenTracing,
      Cinnamon.library.cinnamonOpenTracingJaeger
    ),
    coverageExcludedPackages := "com.github.jmarin.cqrs.lagom.bank.AccountLoader"
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`lagom-bank-api`)

lazy val `lagom-bank-gateway` = (project in file("lagom-bank-gateway"))
  .settings(
    libraryDependencies ++= Seq(
      akkaDiscovery,
      akkaStream,
      akkaHttp
    )
  )
