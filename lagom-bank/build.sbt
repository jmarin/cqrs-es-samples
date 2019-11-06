import com.lightbend.lagom.core.LagomVersion

organization in ThisBuild := "com.github.jmarin"
version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.9"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val akkaDiscovery = "com.lightbend.lagom" %% "lagom-scaladsl-akka-discovery-service-locator" % LagomVersion.current
val akkaKubernetes = "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % "1.0.0"

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
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslAkkaDiscovery,
      lagomScaladslKafkaBroker,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`lagom-bank-api`)
