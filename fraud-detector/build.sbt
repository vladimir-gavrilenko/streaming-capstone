scalaVersion := "2.13.2"
version := "0.1.0-SNAPSHOT"
organization := "com.github"
organizationName := "gva"

lazy val root = (project in file("."))
  .settings(
    name := "fraud-detector"
  )