name := "comptebon"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies += "com.danielasfregola" %% "twitter4s" % "5.5"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.8"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.0"
libraryDependencies += "com.typesafe" % "config" % "1.3.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

lazy val commonSettings = Seq(
  version := "1.0-SNAPSHOT",
  organization := "me.cassayre.florian",
  scalaVersion := "2.12.6",
  mainClass := Some("me.cassayre.florian.comptebon.Main")
)
