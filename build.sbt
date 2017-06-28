name := """pacemakerplay"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"



libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  evolutions,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.google.code.gson" % "gson" % "1.7.1",
  "org.apache.commons" % "commons-lang3" % "3.0",
  "net.sf.flexjson" % "flexjson" % "3.3"
  )
  
libraryDependencies += specs2 % Test
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

fork in run := true
