
enablePlugins(DockerPlugin)

name := "exo1"

version := "1.0"

scalaVersion := "2.11.6"

lazy val akkaVersion = "2.4.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "joda-time" % "joda-time" % "2.9.4",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

// Docker config

maintainer in Docker := "Tugdual Sarazin <tugdual.sarazin@gmail.com>"

dockerBaseImage := "java:8"

dockerExposedVolumes := Seq("/opt/docker/logs")