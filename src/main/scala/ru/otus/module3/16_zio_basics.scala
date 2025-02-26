package ru.otus.module3

import zio._


import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try



/** **
 * ZIO[-R, +E, +A] ----> R => Either[E, A]
 *
 */


object toyModel {


  /**
   * Используя executable encoding реализуем свой zio
   */


  /**
   * Реализуем конструкторы под названием effect и fail
   */


  /** *
   * Напишите консольное echo приложение с помощью нашего игрушечного ZIO
   */





  type Error
  type Environment



  lazy val _: Task[Int] = ???
  lazy val _: IO[Error, Int] = ???
  lazy val _: RIO[Environment, Int] = ???
  lazy val _: URIO[Environment, Int] = ???
  lazy val _: UIO[Int] = ???
}

object zioConstructors {


  // не падающий эффект


  // любой эффект



  // Из Future
  lazy val f: Future[Int] = ???


  // Из try
  lazy val t: Try[String] = ???



  // Из option
  lazy val opt : Option[Int] = ???



  type User
  type Address


  def getUser(): Task[Option[User]] = ???
  def getAddress(u: User): Task[Option[Address]] = ???




  // Из either
  lazy val e: Either[String, Int] = ???






  // особые версии конструкторов


}



object zioOperators {

  /** *
   *
   * 1. Создать ZIO эффект который будет читать строку из консоли
   */

  lazy val readLine = ???

  /** *
   *
   * 2. Создать ZIO эффект который будет писать строку в консоль
   */

  def writeLine(str: String) = ???

  /** *
   * 3. Создать ZIO эффект котрый будет трансформировать эффект содержащий строку в эффект содержащий Int
   */

  lazy val lineToInt = ???
  /** *
   * 3.Создать ZIO эффект, который будет работать как echo для консоли
   *
   */

  lazy val echo = ???

  /**
   * Создать ZIO эффект, который будет привествовать пользователя и говорить, что он работает как echo
   */

  lazy val greetAndEcho = ???



  // greet and echo улучшенный
  lazy val _: ZIO[Any, Throwable, Unit] = ???


  /**
   * Используя уже созданные эффекты, написать программу, которая будет считывать поочереди считывать две
   * строки из консоли, преобразовывать их в числа, а затем складывать их
   */

  lazy val r1 = ???

  /**
   * Второй вариант
   */

  lazy val r2: ZIO[Any, Throwable, Int] = ???

  /**
   * Доработать написанную программу, чтобы она еще печатала результат вычисления в консоль
   */

  lazy val r3 = ???


  lazy val a: Task[Unit] = ???
  lazy val b: Task[String] = ???

  /**
   * последовательная комбинация эффектов a и b
   */
  lazy val ab1 = ???

  /**
   * последовательная комбинация эффектов a и b
   */
  lazy val ab2 = ???

  /**
   * последовательная комбинация эффектов a и b
   */
  lazy val ab3 = ???


  /**
   * Последовательная комбинация эффекта b и b, при этом результатом должна быть конкатенация
   * возвращаемых значений
   */
  lazy val ab4 = ???




  /**
    * 
    * A as B
    */

  lazy val c = ???

}
