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

  "org.typelevel" %% "cats-core" % "2.1.0",

  "org.typelevel" %% "spire" % "0.17.0",

  "org.nd4j" % "nd4j-api" % "1.0.0-M2",
  "org.nd4j" % "nd4j-native" % "1.0.0-M2",

  "org.scalactic" %% "scalactic" % "3.2.14",
  "org.scalatest" %% "scalatest" % "3.2.14" % "test"
)

//resolvers += Resolver.sonatypeRepo("snapshots")

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.1" cross CrossVersion.full)
