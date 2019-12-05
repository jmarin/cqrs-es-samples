import com.lightbend.lagom.core.LagomVersion

organization in ThisBuild := "com.github.jmarin"
version in ThisBuild := "1.0.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.9"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val akkaDiscovery = "com.lightbend.lagom" %% "lagom-scaladsl-akka-discovery-service-locator" % LagomVersion.current
val akkaKubernetes = "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % "1.0.5"
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
  .enablePlugins(LagomScala)
  .settings(
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
      h2,
      postgres
    )
  )
  .dependsOn(`lagom-bank-api`)
