name := "ScottyQML"

version := "0.1"

scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  "xyz.entangled" %% "scotty" % "0.6.0",

  "org.scalanlp" %% "breeze" % "1.2",
  "org.scalanlp" %% "breeze-viz" % "1.2"
)
