ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.2"

lazy val root = (project in file("."))
  .settings(
    name             := "mpc",
    idePackagePrefix := Some("org.treemage")
  )
  .aggregate(core, json)

lazy val core = (project in file("mpc-core")).settings(
  name             := "mpc-core",
  idePackagePrefix := Some("org.treemage")
)

lazy val json = (project in file("mpc-json"))
  .settings(
    name             := "mpc-json",
    idePackagePrefix := Some("org.treemage")
  )
  .dependsOn(core)
