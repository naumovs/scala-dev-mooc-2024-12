package ru.otus.module3.catsconcurrency.cats_effect_homework

import cats.effect.std.Supervisor
import cats.effect.{Concurrent, IO, IOApp}
import cats.implicits._

import scala.concurrent.duration.DurationInt

// Поиграемся с кошельками на файлах и файберами.

// Нужно написать программу где инициализируются три разных кошелька и для каждого из них работает фоновый процесс,
// который регулярно пополняет кошелек на 100 рублей раз в определенный промежуток времени. Промежуток надо сделать разный, чтобы легче было наблюдать разницу.
// Для определенности: первый кошелек пополняем раз в 100ms, второй каждые 500ms и третий каждые 2000ms.
// Помимо этих трёх фоновых процессов (подсказка - это файберы), нужен четвертый, который раз в одну секунду будет выводить балансы всех трех кошельков в консоль.
// Основной процесс программы должен просто ждать ввода пользователя (IO.readline) и завершить программу (включая все фоновые процессы) когда ввод будет получен.
// Итого у нас 5 процессов: 3 фоновых процесса регулярного пополнения кошельков, 1 фоновый процесс регулярного вывода балансов на экран и 1 основной процесс просто ждущий ввода пользователя.

// Можно делать всё на IO, tagless final тут не нужен.

// Подсказка: чтобы сделать бесконечный цикл на IO достаточно сделать рекурсивный вызов через flatMap:
// def loop(): IO[Unit] = IO.println("hello").flatMap(_ => loop())
object WalletFibersApp extends IOApp.Simple {

  def run: IO[Unit] =
    for {
      _ <- IO.println("Press enter to stop...") // Не понял как считать только один символ из консоли. Всегда ждёт enter
      wallet1 <- Wallet.fileWallet[IO]("1")
      wallet2 <- Wallet.fileWallet[IO]("2")
      wallet3 <- Wallet.fileWallet[IO]("3")
      // todo: запустить все файберы и ждать ввода от пользователя чтобы завершить работу
      _ <- Supervisor[IO](await = false).use { supervisor =>
        for {
          _ <- supervisor.supervise[Unit](wallet1.topup(100.0).andWait(100.millis).foreverM)
          _ <- supervisor.supervise[Unit](wallet2.topup(100.0).andWait(500.millis).foreverM)
          _ <- supervisor.supervise[Unit](wallet3.topup(100.0).andWait(2000.millis).foreverM)
          _ <- supervisor.supervise[Unit]{
                 val b = for {
                          balance1 <- wallet1.balance
                          balance2 <- wallet2.balance
                          balance3 <- wallet3.balance
                        } yield println(s"balance1=$balance1, balance2=$balance2, balance3=$balance3")
                 b.andWait(1.second).foreverM
               }
          _ <- IO.readLine
        } yield ()
      }
    } yield ()

}
