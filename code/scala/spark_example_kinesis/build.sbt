scalaVersion := "2.12.12"

name := "spark_example_kinesis"
version := "1.0"

// Estas bibliotecas existen por defecto en la distribucion de spark
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.0.0"

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.20"

// provided by --packages org.apache.spark:spark-streaming-kinesis-asl_2.12:3.0.0
libraryDependencies += "org.apache.spark" %% "spark-streaming-kinesis-asl" % "3.0.0"

libraryDependencies += "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.7.1"

// Requeridos para operaciones de s3 a través de hadoop
libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "3.2.1"
libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % "3.2.1"

// libraryDependencies += "org.apache.parquet" %% "parquet-avro" % "1.11.1"
// version 2 de sdk
//libraryDependencies += "software.amazon.awssdk" % "s3" % "2.14.19"

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
