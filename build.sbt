ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.0"

scalacOptions += "-Ypartial-unification"
lazy val root = (project in file("."))
  .settings(
    name := "course-asmd23-models",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.12" % Test,
      "org.scalacheck" %% "scalacheck" % "1.17.0",
      ("de.sciss" %% "scala-chart" % "0.8.0").cross(CrossVersion.for2_13Use3),
      "org.typelevel" %% "cats-core" % "2.12.0",
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
      "net.aichler" % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test,
      "org.mockito" % "mockito-core" % "3.+" % Test,
      "io.cucumber" % "cucumber-java" % "6.1.1" % Test
    ),
    Compile / scalaSource := baseDirectory.value / "src" / "main",
    Test / scalaSource := baseDirectory.value / "src" / "test",

  )
