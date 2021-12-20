ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "2.12.12"

lazy val sparkVersion       = "3.2.0"
lazy val circeVersion       = "0.14.1"
lazy val pulsar4sVersion    = "2.8.1"
lazy val pulsarSparkVersion = "3.1.1.3"

val ingestionDependencies = Seq(
  "com.clever-cloud.pulsar4s" % "pulsar4s-core_2.12"  % pulsar4sVersion,
  "com.clever-cloud.pulsar4s" % "pulsar4s-circe_2.12" % pulsar4sVersion
)

val analysisDependencies = Seq(
  "io.circe"                    %%  "circe-core"                  %   circeVersion,
  "io.circe"                    %%  "circe-generic"               %   circeVersion,
  "io.circe"                    %%  "circe-parser"                %   circeVersion,
  "org.apache.spark"            %%  "spark-sql"                   % sparkVersion,
  "io.streamnative.connectors"  %   "pulsar-spark-connector_2.12" % pulsarSparkVersion
)

lazy val root = (project in file("."))
  .settings(
    name := "pulsar-spark-streaming-analytics"
  )

lazy val `ingestion-layer` = (project in file("ingestion-layer"))
  .settings(
    libraryDependencies ++= ingestionDependencies
  )


lazy val `analysis-layer` = (project in file("analysis-layer"))
  .settings(
    libraryDependencies ++=analysisDependencies
  )
