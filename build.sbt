val Versions =
  new {
    val http4s        = "0.23.3"
    val cats          = "3.2.9"
    val newtype       = "0.4.4"
    val squants       = "1.8.3"
  }

version := "0.1"

ThisBuild / scalaVersion := "2.13.6"

Compile / mainClass := Some("lgajowy.shop.Main")

scalacOptions ++= List("-Ymacro-annotations")

libraryDependencies ++= Seq(
  "org.typelevel"  %% "cats-effect"         % Versions.cats,
  "org.http4s"     %% "http4s-dsl"          % Versions.http4s,
  "org.http4s"     %% "http4s-blaze-server" % Versions.http4s,
  "io.estatico"    %% "newtype"             % Versions.newtype,
  "org.typelevel"  %% "squants"             % Versions.squants,
)
