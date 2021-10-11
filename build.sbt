val Versions =
  new {
    val http4s = "0.23.3"
    val cats = "3.2.9"
  }

version := "0.1"

ThisBuild / scalaVersion := "2.13.6"

Compile / mainClass := Some("lgajowy.shop.Main")

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % Versions.cats,
  "org.http4s" %% "http4s-dsl" % Versions.http4s,
  "org.http4s" %% "http4s-blaze-server" % Versions.http4s,
)