lazy val root = (project in file(".")).
  settings(
    name := "quill-playground",
    organization := "cerst",
    version := "0.0.0",
    scalaVersion := "2.11.8",

    // for details see: http://www.scala-lang.org/files/archive/nightly/docs/manual/html/scalac.html
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint", "-encoding", "utf8", "-target:jvm-1.8"),

    libraryDependencies ++= Dependencies.libraries
  )

