organization := "org.srethink"

name := "srethink"

version := "0.0.1"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.10.4", "2.11.2")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies <<= scalaVersion{ scalaVersion =>
  val Some(majorV) = CrossVersion.partialVersion(scalaVersion)
  val playV = "2.3.3"
  val shared = Seq(
    "org.scala-lang"    % "scala-reflect" % scalaVersion,
    "io.netty"          % "netty"         % "3.9.2.Final",
    "com.typesafe.play" %% "play-json"    % playV % "provided",
    "com.typesafe.play" %% "play"         % playV % "provided",
    "org.slf4j"         % "slf4j-api"     % "1.7.7",
    "org.specs2"        %% "specs2"       % "2.3.13" % "test",
    "org.slf4j"         % "slf4j-simple"  % "1.7.7" % "test"
  )
  majorV match {
    case (2, 10) =>
      shared ++ Seq(
        compilerPlugin("org.scalamacros" % "paradise" % "2.0.0" cross CrossVersion.full),
        "org.scalamacros" %% "quasiquotes" % "2.0.0")
    case (2, 11) =>
      shared
  }
}

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-language:implicitConversions",
  "-language:dynamics",
  "-language:higherKinds"
)

ScoverageKeys.minimumCoverage := 80

ScoverageKeys.failOnMinimumCoverage := true

ScoverageKeys.highlighting := {
  if (scalaBinaryVersion.value == "2.10") false
  else false
}

publishArtifact in Test := false

parallelExecution in Global := false

instrumentSettings

coverallsSettings

ScoverageKeys.excludedPackages in ScoverageCompile := "srethink\\.protocol\\..*"
