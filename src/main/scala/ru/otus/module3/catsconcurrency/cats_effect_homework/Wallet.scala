package ru.otus.module3.catsconcurrency.cats_effect_homework

import cats.effect.{IO, Sync}
import cats.implicits._
import Wallet.{WalletError, _}
import cats.data.EitherT
import cats.{Functor, Group}
import cats.effect.kernel.Resource.{Pure, both}
import cats.kernel.Semigroup

// DSL управления электронным кошельком
trait Wallet[F[_]] {
  // возвращает текущий баланс
  def balance: F[BigDecimal]
  // пополняет баланс на указанную сумму
  def topup(amount: BigDecimal): F[Unit]
  // списывает указанную сумму с баланса (ошибка если средств недостаточно)
  def withdraw(amount: BigDecimal): F[Either[WalletError, Unit]]
}

// Игрушечный кошелек который сохраняет свой баланс в файл
// todo: реализовать используя java.nio.file._
// Насчёт безопасного конкуррентного доступа и производительности не заморачиваемся, делаем максимально простую рабочую имплементацию. (Подсказка - можно читать и сохранять файл на каждую операцию).
// Важно аккуратно и правильно завернуть в IO все возможные побочные эффекты.
//
// функции которые пригодятся:
// - java.nio.file.Files.write
// - java.nio.file.Files.readString
// - java.nio.file.Files.exists
// - java.nio.file.Paths.get
final class FileWallet[F[_]: Sync](id: WalletId) extends Wallet[F] {

  import java.nio.file._

  private val tmpDir = scala.util.Properties.envOrElse("TMP", "/tmp")

  override def balance: F[BigDecimal] = Sync[F].delay {
    if (Files.exists(Paths.get(s"$tmpDir/$id"))) {
      BigDecimal(
        Files.readString(Paths.get(s"$tmpDir/$id"))
      )
    } else {
      0
    }
  }
  override def topup(amount: BigDecimal): F[Unit] = for {
    currentBalance <- balance
    _ <- Sync[F].delay {
      if (Files.exists(Paths.get(s"$tmpDir/$id"))) {
        Files.writeString(Paths.get(s"$tmpDir/$id"), Semigroup[BigDecimal]
          .combine(currentBalance, amount).toString)
      } else {
        Files.writeString(Paths.get(s"$tmpDir/$id"), amount.toString,
          StandardOpenOption.CREATE)
      }
    }
  } yield ()

  override def withdraw(amount: BigDecimal): F[Either[WalletError, Unit]] = for {
    currentBalance <- balance
    approved = currentBalance >= amount
    _ <- Sync[F].whenA(approved)(topup(-amount))
  } yield if (approved) Right(()) else Left(BalanceTooLow)
}

object Wallet {

  // todo: реализовать конструктор
  // внимание на сигнатуру результата - инициализация кошелька имеет сайд-эффекты
  // Здесь нужно использовать обобщенную версию уже пройденного вами метода IO.delay,
  // вызывается она так: Sync[F].delay(...)
  // Тайпкласс Sync из cats-effect описывает возможность заворачивания сайд-эффектов
  def fileWallet[F[_]: Sync](id: WalletId): F[Wallet[F]] = {
    for {
      wallet <- Sync[F].delay(new FileWallet[F](id))
    } yield wallet
  }

  type WalletId = String

  sealed trait WalletError
  case object BalanceTooLow extends WalletError
}
