package ru.otus.module3

import ru.otus.module3.zioConcurrency.{currentTime, printEffectRunningTime}
import ru.otus.module3.zio_homework.config.{AppConfig, Configuration}

import scala.language.postfixOps
import zio._

import java.util.concurrent.TimeUnit
import scala.util.Try

package object zio_homework {
  /**
   * 1.
   * Используя сервисы Random и Console, напишите консольную ZIO программу которая будет предлагать пользователю угадать число от 1 до 3
   * и печатать в консоль угадал или нет. Подумайте, на какие наиболее простые эффекты ее можно декомпозировать.
   */


  lazy val guessProgram = for {
    random <- Random.nextIntBetween(1, 4)
    userGuess <- Console.readLine("What number am I thinking?\n")
    touchInt <- ZIO.fromTry(Try(userGuess.toInt)).orElseFail("Numbers only")
    _ <- if (random == touchInt) Console.printLine("yep!") else Console.printLine(s"It was $random. Try again")
  } yield ()

  /**
   * 2. реализовать функцию doWhile (общего назначения), которая будет выполнять эффект до тех пор, пока его значение в условии не даст true
   * 
   */

  def doWhile[R, E, A, B](predicate: A => Boolean)(effect: ZIO[R, E, A]): ZIO[R, E, A] =
    effect.repeatUntil(predicate)

  /**
   * 3. Реализовать метод, который безопасно прочитает конфиг из переменных окружения, а в случае ошибки вернет дефолтный конфиг
   * и выведет его в консоль
   * Используйте эффект "Configuration.config" из пакета config
   */


  def loadConfigOrDefault: ZIO[Any, Throwable, Unit] = for {
    host <- Configuration.config.map(_.host).orElse(ZIO.succeed("localhost"))
    port <- Configuration.config.map(_.port).orElse(ZIO.succeed("8080"))
    _ <- Console.printLine(s"Host: $host, port: $port")
  } yield ()


  /**
   * 4. Следуйте инструкциям ниже для написания 2-х ZIO программ,
   * обратите внимание на сигнатуры эффектов, которые будут у вас получаться,
   * на изменение этих сигнатур
   */


  /**
   * 4.1 Создайте эффект, который будет возвращать случайным образом выбранное число от 0 до 10 спустя 1 секунду
   * Используйте сервис zio Random
   */
  lazy val eff: UIO[Int] = Random.nextIntBetween(0, 10).delay(1.seconds)

  /**
   * 4.2 Создайте коллукцию из 10 выше описанных эффектов (eff)
   */
  lazy val effects: List[UIO[Int]] = List.fill(10)(eff)

  
  /**
   * 4.3 Напишите программу которая вычислит сумму элементов коллекции "effects",
   * напечатает ее в консоль и вернет результат, а также залогирует затраченное время на выполнение,
   * можно использовать ф-цию printEffectRunningTime, которую мы разработали на занятиях
   */

  lazy val app = printEffectRunningTime {
    for {
    sum <- ZIO.foreach(effects)(identity).map(_.sum)
      _ <- Console.printLine(s"Sum: $sum")
    } yield ZIO.succeed(sum)
  }

  /**
   * 4.4 Усовершенствуйте программу 4.3 так, чтобы минимизировать время ее выполнения
   */

  lazy val appSpeedUp = printEffectRunningTime {
    for {
      sum <- ZIO.foreachPar(effects)(identity).map(_.sum)
      _ <- Console.printLine(s"Sum: $sum")
    } yield ZIO.succeed(sum)
  }


  /**
   * 5. Оформите ф-цию printEffectRunningTime разработанную на занятиях в отдельный сервис, так чтобы ее
   * можно было использовать аналогично zio.Console.printLine например
   */



  trait RunningTime {
    def printEffectRunningTime[R, E, A](zio: ZIO[R, E, A]): ZIO[R, E, A]
  }

  object RunningTimeLive extends RunningTime {

    val currentTime: UIO[Long] = Clock.currentTime(TimeUnit.SECONDS)

    override def printEffectRunningTime[R, E, A](zio: ZIO[R, E, A]): ZIO[R, E, A] = for{
      start <- currentTime
      r <- zio
      end <- currentTime
      _ <- Console.printLine(s"Running time ${end - start}").orDie
    } yield r

  }

  def printEffectRunningTimeAsService[R, E, A](zio: ZIO[R, E, A]): ZIO[R with RunningTime, E, A] =
    ZIO.serviceWithZIO[RunningTime](_.printEffectRunningTime(zio))


   /**
     * 6.
     * Воспользуйтесь написанным сервисом, чтобы создать эффект, который будет логировать время выполнения программы из пункта 4.3
     *
     * 
     */


  val environment =
    ZEnvironment[Console, Clock, RunningTime](Console.ConsoleLive, Clock.ClockLive, RunningTimeLive)


  lazy val appWithTimeLogg = printEffectRunningTimeAsService(app)

  /**
    * 
    * Подготовьте его к запуску и затем запустите воспользовавшись ZioHomeWorkApp
    */

  lazy val runApp = appWithTimeLogg.provideEnvironment(environment)

}
