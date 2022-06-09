name := "ScottyQML"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "xyz.entangled" %% "scotty" % "0.6.0",

  //"org.scalanlp" %% "breeze" % "1.2",
  //"org.scalanlp" %% "breeze-viz" % "1.2",
  "org.scalanlp" %% "breeze" % "2.0.1-RC1",

  // The visualization library is distributed separately as well.
  // It depends on LGPL code
  "org.scalanlp" %% "breeze-viz" % "2.0.1-RC1",

  "org.typelevel" %% "cats-core" % "2.1.0"
)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.1" cross CrossVersion.full)
