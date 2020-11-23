organization in ThisBuild := "com.github.jmarin"

version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.13.4"

val AkkaVersion = "2.6.10"
val AkkaHttpVersion = "10.2.1"

val scalaTest = "org.scalatest" %% "scalatest" % "3.2.2" % Test
val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.14.1" % Test
val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % AkkaVersion
val akkagRPC = "com.typesafe.akka" %% "akka-grpc" % AkkaVersion
val akkaHttp = "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion

val CommonDeps = Seq(scalaTest, scalaCheck)
val AkkaDeps = Seq(akkaCluster, akkagRPC)

val backend = (project in file("backend"))
  .settings(
    libraryDependencies ++= CommonDeps ++ AkkaDeps
  )

val gateway = (project in file("gateway"))
  .settings(
    libraryDependencies ++= Seq(akkaHttp)
  )
