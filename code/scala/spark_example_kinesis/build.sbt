scalaVersion := "2.12.12"

name := "spark_example_kinesis"
version := "1.0"

// Use provided with spark-submit
// libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.0.0" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.0.0"

libraryDependencies += "org.apache.spark" %% "spark-streaming-kinesis-asl" % "3.0.0"

libraryDependencies += "org.apache.parquet" % "parquet-avro" % "1.11.1"

// ============================================================================

// Here's a quick glimpse of what a multi-project build looks like for this
// build, with only one "subproject" defined, called `root`:

// lazy val root = (project in file(".")).
//   settings(
//     inThisBuild(List(
//       organization := "ch.epfl.scala",
//       scalaVersion := "2.13.1"
//     )),
//     name := "hello-world"
//   )

// To learn more about multi-project builds, head over to the official sbt
// documentation at http://www.scala-sbt.org/documentation.html
