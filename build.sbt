name := "wkt-geometry-parser"

version := "1.0"

scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging)

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.4",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "com.typesafe.play" % "play-json_2.11" % "2.5.4",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)