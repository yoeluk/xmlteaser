name := "xmlteaser"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

scalacOptions ++= Seq(
  "-language:_",
  "-feature",
  "-Xfatal-warnings",
  "-deprecation",
  "-unchecked"
)

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.2.5",
  "org.scalaz" %% "scalaz-core" % "7.2.0",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
  "com.novocode" % "junit-interface" % "0.7" % "test",
  "com.typesafe.play" %% "play-json" % "2.3.4" % "test"
)
    