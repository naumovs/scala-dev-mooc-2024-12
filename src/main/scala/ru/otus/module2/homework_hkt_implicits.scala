package ru.otus.module2

object homework_hkt_implicits{

  trait Bindable[F[_], A] {
    def map[B](f: A => B): F[B]
    def flatMap[B](f: A => F[B]): F[B]
  }

  object Bindable {
    // Неявное преобразование контейнера F[A] в Bindable[F, A]
    implicit def toBindable[F[_], A](fa: F[A])(implicit ev: BindableInstance[F]): Bindable[F, A] =
      new Bindable[F, A] {
        def map[B](f: A => B): F[B] = ev.map(fa)(f)
        def flatMap[B](f: A => F[B]): F[B] = ev.flatMap(fa)(f)
      }
  }

  // Интерфейс для работы с контейнером F[_]
  trait BindableInstance[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  }

  import Bindable.toBindable

  // Реализация метода tuplef
  def tuplef[F[_], A, B](fa: F[A], fb: F[B])(implicit ev: BindableInstance[F]): F[(A, B)] = {
    for {
      a <- fa
      b <- fb
    } yield (a, b)
  }

  // Пример для списков
  implicit val listBindableInstance: BindableInstance[List] = new BindableInstance[List] {
    def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
    def flatMap[A, B](fa: List[A])(f: A => List[B]): List[B] = fa.flatMap(f)
  }

  val tuple1: Seq[(Int, Int)] = tuplef(List(1, 2, 3), List(4, 5, 6))
}