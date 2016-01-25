name := "xmlteaser"
organization := "com.briefscala"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

scalacOptions ++= Seq(
  "-language:_",
  "-feature",
  "-Xfatal-warnings",
  "-deprecation",
  "-unchecked"
)

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
)

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.2.5",
  "org.scalaz" %% "scalaz-core" % "7.2.0",
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.3",
  "com.novocode" % "junit-interface" % "0.7" % "test",
  "com.typesafe.play" %% "play-json" % "2.3.4" % "test"
)
