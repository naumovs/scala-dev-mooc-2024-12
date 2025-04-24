package ru.otus.module4

import cats.data.Kleisli
import cats.effect.{IO, IOApp, Ref, Resource}
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.server.Server
import cats.effect.std.Console
import com.comcast.ip4s.{Host, Port}
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object Http4sHomeWork {

  case class CounterResponse(counter: Int)

  implicit val encoder: Encoder[CounterResponse] = deriveEncoder

  object RestApi {
    def publicRoutes(counterRef: Ref[IO, Int]): HttpRoutes[IO] = HttpRoutes.of {
      case GET -> Root / "counter" =>
        for {
          _ <- counterRef.update(_ + 1) // Увеличиваем счетчик
          current <- counterRef.get     // Получаем текущее значение
          response <- Ok(CounterResponse(current)) // Возвращаем ответ
        } yield response
    }
  }

  def router(counterRef: Ref[IO, Int]): HttpRoutes[IO] =
    Router("/api" -> RestApi.publicRoutes(counterRef))

  implicit val loggerFactory: LoggerFactory[IO] = Slf4jFactory.create[IO]

  def loggerService(counterRef: Ref[IO, Int]): Kleisli[IO, Request[IO], Response[IO]] = Logger.httpRoutes[IO](
    logHeaders = false,
    logBody = true,
    redactHeadersWhen = _ => false,
    logAction = Some((msg: String) => Console[IO].println(msg))
  )(router(counterRef)).orNotFound

  def server(counterRef: Ref[IO, Int]): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(loggerService(counterRef))
      .build
}

object Http4sHomeWorkServer extends IOApp.Simple {
  override def run: IO[Unit] = for {
    counterRef <- Ref[IO].of(0)
    _ <- Http4sHomeWork.server(counterRef).use(_ => IO.never)
  } yield ()
}
