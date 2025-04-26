package ru.otus.module4

import cats.data.Kleisli
import cats.effect.std.Console
import cats.effect.{IO, IOApp, Resource}
import com.comcast.ip4s.{Host, Port}
import fs2.Stream
import fs2.io.file.{Files, Flag, Flags, Path}
import org.http4s.TransferCoding.chunked
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.{`Content-Type`, `Transfer-Encoding`}
import org.http4s.server.middleware.Logger
import org.http4s.server.{Router, Server}
import org.http4s.{HttpRoutes, MediaType, Request, Response}
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

import scala.concurrent.duration
import scala.concurrent.duration.MILLISECONDS


object Http4sHomeWorkSlow {

  object RestApi {

    private def streamResponse(filePath: Path, chunkSize: Int, totalBytes: Int, delayMillis: Long): Stream[IO, Byte] = {

      val fileStream = Files[IO].readAll(filePath, chunkSize, Flags(Flag.Read))

      // задержка между прочтением каждого чанка
      val delayedFileStream: Stream[IO, Byte] = fileStream.evalMap { bytes =>
        IO.sleep(duration.Duration(delayMillis, MILLISECONDS)) *> IO.pure(bytes)
      }
      // стоп, когда передано нужное количество байт
      delayedFileStream.take(totalBytes)
    }

    def publicRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
      case GET -> Root / "slow" / chunkStr / totalStr / timeStr =>
        (for {
          chunk <- IO.fromOption(chunkStr.toIntOption.filter(_ > 0))(new IllegalArgumentException("Invalid chunk size"))
          total <- IO.fromOption(totalStr.toIntOption.filter(_ > 0))(new IllegalArgumentException("Invalid total size"))
          time  <- IO.fromOption(timeStr.toIntOption.filter(_ > 0))(new IllegalArgumentException("Invalid time interval"))
        } yield (chunk, total, time)).attempt.flatMap {
          case Right((chunk, total, time)) =>
            val tmpDir = scala.util.Properties.envOrElse("TMP", "/tmp")
            val randomStream: Stream[IO, Byte] =
              streamResponse(Path(s"${tmpDir}/textfile.txt"), chunk, total, time)

            Ok(randomStream)
              .map(_.putHeaders(`Content-Type`(MediaType.text.plain), `Transfer-Encoding`(chunked)))

          case Left(_) =>
            BadRequest("Invalid parameters. Ensure all values are positive integers.")
        }
    }
  }

  def router: HttpRoutes[IO] =
    Router("/api" -> RestApi.publicRoutes)

  implicit val loggerFactory: LoggerFactory[IO] = Slf4jFactory.create[IO]

  def loggerService: Kleisli[IO, Request[IO], Response[IO]] = Logger.httpRoutes[IO](
    logHeaders = false,
    logBody = true,
    redactHeadersWhen = _ => false,
    logAction = Some((msg: String) => Console[IO].println(msg))
  )(router).orNotFound

  def server: Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(loggerService)
      .build
}

object Http4sHomeWorkSlowServer extends IOApp.Simple {
  override def run: IO[Unit] = for {
    _ <- Http4sHomeWorkSlow.server.use(_ => IO.never)
  } yield ()
}
