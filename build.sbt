val Versions =
  new {
    val http4s        = "0.23.3"
    val http4sCirce   = "0.23.3"
    val cats          = "3.2.9"
    val log4cats      = "2.1.1"
    val catsRetry     = "3.1.0"
    val newtype       = "0.4.4"
    val squants       = "1.8.3"
    val derevo        = "0.12.6"
    val refined       = "0.9.27"
    val circe         = "0.14.1"
    val http4sJwtAuth = "1.0.0"
    val skunk         = "0.2.2"
  }

version := "0.1"

ThisBuild / scalaVersion := "2.13.6"

Compile / mainClass := Some("lgajowy.shop.Main")

scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info")

libraryDependencies ++= Seq(
  "org.typelevel"    %% "cats-effect"           % Versions.cats,
  "org.http4s"       %% "http4s-dsl"            % Versions.http4s,
  "org.http4s"       %% "http4s-blaze-server"   % Versions.http4s,
  "org.http4s"       %% "http4s-circe"          % Versions.http4sCirce,
  "io.estatico"      %% "newtype"               % Versions.newtype,
  "org.typelevel"    %% "squants"               % Versions.squants,
  "com.github.cb372" %% "cats-retry"            % Versions.catsRetry,
  "org.typelevel"    %% "log4cats-slf4j"        % Versions.log4cats,
  "tf.tofu"          %% "derevo-core"           % Versions.derevo,
  "tf.tofu"          %% "derevo-cats"           % Versions.derevo,
  "tf.tofu"          %% "derevo-circe-magnolia" % Versions.derevo,
  "eu.timepit"       %% "refined"               % Versions.refined,
  "eu.timepit"       %% "refined-cats"          % Versions.refined,
  "io.circe"         %% s"circe-core"           % Versions.circe,
  "io.circe"         %% s"circe-generic"        % Versions.circe,
  "io.circe"         %% s"circe-parser"         % Versions.circe,
  "io.circe"         %% s"circe-refined"        % Versions.circe,
  "dev.profunktor"   %% "http4s-jwt-auth"       % Versions.http4sJwtAuth,
  "org.tpolecat"     %% "skunk-core"            % Versions.skunk,
  "org.tpolecat"     %% "skunk-circe"           % Versions.skunk
)
