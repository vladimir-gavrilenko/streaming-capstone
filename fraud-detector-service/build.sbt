ThisBuild / scalaVersion := "2.11.12"
ThisBuild / organization := "com.github"

lazy val sparkVersion = "2.4.5"

lazy val root = (project in file("."))
  .settings(
    name := "fraud-detector-service",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-streaming" % sparkVersion % Provided,
      "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
      "com.github.scopt" %% "scopt" % "3.7.1",
      "com.holdenkarau" %% "spark-testing-base" % s"${sparkVersion}_0.14.0" % Test
    ),
    assemblyJarName in assembly := "fraud-detector-service.jar",
    fork in Test := true,
    parallelExecution in Test := false,
    javaOptions ++= Seq(
      "-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"
    )
  )
