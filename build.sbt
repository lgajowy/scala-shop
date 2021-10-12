val Versions =
  new {
    val http4s    = "0.23.3"
    val cats      = "3.2.9"
    val log4cats  = "2.1.1"
    val catsRetry = "3.1.0"
    val newtype   = "0.4.4"
    val squants   = "1.8.3"
  }

version := "0.1"

ThisBuild / scalaVersion := "2.13.6"

Compile / mainClass := Some("lgajowy.shop.Main")

scalacOptions ++= List("-Ymacro-annotations", "-Wconf:cat=unused:info")

libraryDependencies ++= Seq(
  "org.typelevel"    %% "cats-effect"         % Versions.cats,
  "org.http4s"       %% "http4s-dsl"          % Versions.http4s,
  "org.http4s"       %% "http4s-blaze-server" % Versions.http4s,
  "io.estatico"      %% "newtype"             % Versions.newtype,
  "org.typelevel"    %% "squants"             % Versions.squants,
  "com.github.cb372" %% "cats-retry"          % Versions.catsRetry,
  "org.typelevel"    %% "log4cats-slf4j"      % Versions.log4cats
)
