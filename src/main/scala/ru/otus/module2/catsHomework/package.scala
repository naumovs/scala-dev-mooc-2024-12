package ru.otus.module2

import cats.Functor
import cats.data.EitherT

import scala.util.{Failure, Success, Try}


package object catsHomework {

  /**
   * Простое бинарное дерево
   * @tparam A
   */
  sealed trait Tree[+A]
  final case class Branch[A](left: Tree[A], right: Tree[A])
    extends Tree[A]
  final case class Leaf[A](value: A) extends Tree[A]

  /**
   * Напишите instance Functor для объявленного выше бинарного дерева.
   * Проверьте, что код работает корректно для Branch и Leaf
   */

   lazy val treeFunctor = new Functor[Tree] {
     override def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = fa match {
       case Leaf(value) => Leaf(f(value))
       case Branch(left, right) => Branch(map(left)(f), map(right)(f))
     }
   }

   // Проверочный код в Main
   // println(treeFunctor.map(Branch(Leaf(1), Branch(Leaf(2), Leaf(3))))(i => i + 1))

     /**
      * Monad абстракция для последовательной
      * комбинации вычислений в контексте F
      *
      * @tparam F
      */
     trait Monad[F[_]] {
       def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

       def pure[A](v: A): F[A]
     }


     /**
      * MonadError расширяет возможность Monad
      * кроме последовательного применения функций, позволяет обрабатывать ошибки
      *
      * @tparam F
      * @tparam E
      */
     trait MonadError[F[_], E] extends Monad[F] {
       // Поднимаем ошибку в контекст `F`:
       def raiseError[A](e: E): F[A]

       // Обработка ошибки, потенциальное восстановление:
       def handleErrorWith[A](fa: F[A])(f: E => F[A]): F[A]

       // Обработка ошибок, восстановление от них:
       def handleError[A](fa: F[A])(f: E => A): F[A]

       // Test an instance of `F`,
       // failing if the predicate is not satisfied:
       def ensure[A](fa: F[A])(e: E)(f: A => Boolean): F[A]
     }

     /**
      * Напишите instance MonadError для Try
      */

     lazy val tryME = new MonadError[Try, Throwable] {
       override def raiseError[A](e: Throwable): Try[A] = Failure(e)

       override def handleErrorWith[A](fa: Try[A])(f: Throwable => Try[A]): Try[A] = fa match {
         case Failure(e) => f(e)
         case _ => fa
       }

       override def handleError[A](fa: Try[A])(f: Throwable => A): Try[A] = fa match {
         case Failure(e) => Try(f(e))
         case _ => fa
       }

       override def ensure[A](fa: Try[A])(e: Throwable)(f: A => Boolean): Try[A] = fa match {
         case Failure(e) => if(f(fa.get)) fa else Failure(e)
         case _ => fa
       }

       override def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = fa match {
         case Failure(e) => Failure(e)
         case Success(a) => f(a)
       }

       override def pure[A](v: A): Try[A] = Success(v)
     }

     /**
      * Напишите instance MonadError для Either,
      * где в качестве типа ошибки будет String
      */

     type StringEither[A] = Either[String, A]

     val eitherME = new MonadError[StringEither, Throwable]{

       override def raiseError[A](e: Throwable): StringEither[A] = Left(e.getMessage)

       override def handleErrorWith[A](fa: StringEither[A])(f: Throwable => StringEither[A]): StringEither[A] =
         fa match {
           case Left(value) => f {
             new Throwable(value)
           }
           case Right(_) => fa
         }

       override def handleError[A](fa: StringEither[A])(f: Throwable => A): StringEither[A] =
         fa match {
           case Left(value) => Left(f(new Throwable(value)).toString)
           case Right(_) => fa
         }

       override def ensure[A](fa: StringEither[A])(e: Throwable)(f: A => Boolean): StringEither[A] =
         fa match {
           case Left(value) => Left(value)
           case Right(value) => if(f(value)) fa else Left(e.getMessage)
         }

       override def flatMap[A, B](fa: StringEither[A])(f: A => StringEither[B]): StringEither[B] =
         fa match {
           case Left(value) => Left(value)
           case Right(value) => f(value)
         }

       override def pure[A](v: A): StringEither[A] = Right(v)
     }
//
}
