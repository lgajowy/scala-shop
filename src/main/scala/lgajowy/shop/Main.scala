package lgajowy.shop

import cats.effect.{IO, IOApp}
import org.http4s.HttpRoutes
import org.http4s.Method.GET
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.dsl.io._

import scala.concurrent.ExecutionContext.Implicits.global


object Main extends IOApp.Simple {

  override def run: IO[Unit] = {
    val helloWorldService = HttpRoutes.of[IO] {
      case GET -> Root / "hello" / name =>
        Ok(s"Hello, $name.")
    }.orNotFound

    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(helloWorldService)
      .resource
      .useForever
  }
}
