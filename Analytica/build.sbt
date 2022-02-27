ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.1.2",
  "org.apache.spark" %% "spark-sql" % "3.1.2",
  "org.apache.spark" %% "spark-streaming" % "3.1.2",
  "org.apache.kafka" % "kafka-clients" % "3.1.0",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.1.2",
  "mysql" % "mysql-connector-java" % "8.0.27"
)

lazy val root = (project in file("."))
  .settings(
    name := "Analytica"
  )