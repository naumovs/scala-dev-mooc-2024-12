package ru.otus.module1

import scala.::
import scala.annotation.tailrec
import scala.language.postfixOps



/**
 * referential transparency
 */




 // recursion

object recursion {

  /**
   * Реализовать метод вычисления n!
   * n! = 1 * 2 * ... n
   */

  def fact(n: Int): Int = {
    var _n = 1
    var i = 2
    while (i <= n){
      _n *= i
      i += 1
    }
    _n
  }


  def factRec(n: Int): Int = if(n <= 0) 1 else n * factRec(n - 1)


  def factTailRec(n: Int): Int = {
    @tailrec
    def loop(x: Int, accum: Int): Int = {
      if( n <= 0) accum
      else loop(x - 1, x * accum)
    }
    loop(n, 1)
  }




  /**
   * реализовать вычисление N числа Фибоначчи
   * F0 = 0, F1 = 1, Fn = Fn-1 + Fn - 2
   */


}



object hof{

  def dumb(string: String): Unit = {
    Thread.sleep(1000)
    println(string)
  }

  // обертки

  def logRunningTime[A, B](f: A => B): A => B = a => {
    val start = System.currentTimeMillis()
    val result: B = f(a)
    val end = System.currentTimeMillis()
    println(s"Running time: ${end - start}")
    result
  }



  // изменение поведения ф-ции


  def isOdd(i: Int): Boolean = i % 2 > 0

  def not[A](f: A => Boolean): A => Boolean = a => !f(a)

  lazy val isEven: Int => Boolean = not(isOdd)



  // изменение самой функции

  def partial[A, B, C](a: A, f: (A, B) => C): B => C =
    b => f(a, b)

  def partial2[A, B, C](a: A, f: (A, B) => C): B => C = f.curried(a)


  def sum(x: Int, y: Int): Int = x + y


  val p: Int => Int = partial(3, sum)
  p(2) // 5
  p(3) // 5



















}






/**
 *  Реализуем тип Option
 */



 object opt {


  class Animal
  class Dog extends Animal
  class Cat extends Animal

  def treat(animal: Animal): Unit = ???
  def treat(animal: Option[Animal]): Unit = ???

  val d: Dog = ???
  val dOpt: Option[Dog] = ???
  treat(d)
  treat(dOpt)

  /**
   *
   * Реализовать структуру данных Option, который будет указывать на присутствие либо отсутсвие результата
   */

  // Variance
  // 1. Invariance
  // 2. Covariance
  // 3. Contravariance

  trait Option[+T]{
    def isEmpty: Boolean = if(this.isInstanceOf[None.type]) true else false

    def get: T = this match {
      case Some(v) => v
      case None => throw new NoSuchElementException
    }

    def map[B](f: T => B): Option[B] = flatMap(v => Option(f(v)))

    def flatMap[B](f: T => Option[B]): Option[B] = ???

    def zip[B](obj: Option[B]): Option[(T, B)] = ???
  }

  object Option{
    def apply[T](v: T): Option[T] = Some(v)
  }

  val o1: Option[Int] = ???

  val o2: Option[Int] = o1.map(_ + 2)

  case class Some[T](v: T) extends Option[T]
  case object None extends Option[Nothing]

  var o: Option[Animal] = None
  var i: Option[Int] = None







  /**
   *
   * Реализовать метод printIfAny, который будет печатать значение, если оно есть
   */
  def printIfAny[T](v: Option[T]): Unit = if (!v.isEmpty) println(v.get)


  /**
   *
   * Реализовать метод zip, который будет создавать Option от пары значений из 2-х Option
   */
  def zip[A, B](o1: Option[A], o2: Option[B]): Option[(A, B)] = (o1, o2) match {
    case (Some(v1), Some(v2)) => Some((v1, v2))
    case _ => None
  }


  /**
   *
   * Реализовать метод filter, который будет возвращать не пустой Option
   * в случае если исходный не пуст и предикат от значения = true
   */
  def filter(o: Option[Boolean]): Option[Boolean] = if (!o.isEmpty && o.get) o else None

 }

 object list {
   /**
    *
    * Реализовать односвязанный иммутабельный список List
    * Список имеет два случая:
    * Nil - пустой список
    * Cons - непустой, содержит первый элемент (голову) и хвост (оставшийся список)
    */


   trait List[+T]{
     def ::[TT >: T](elem: TT): List[TT] = new ::(elem, this)
   }
   case class ::[T](head: T, tail: List[T]) extends List[T]
   case object Nil extends List[Nothing]

   object List{
     def apply[A](v: A*): List[A] = if(v.isEmpty) Nil
     else new ::(v.head, apply(v.tail:_*))
   }

   val l1: List[Nothing] = List()
   val l2 = List(1, 2, 3)



    /**
      * Конструктор, позволяющий создать список из N - го числа аргументов
      * Для этого можно воспользоваться *
      * 
      * Например вот этот метод принимает некую последовательность аргументов с типом Int и выводит их на печать
      * def printArgs(args: Int*) = args.foreach(println(_))
      */

    /**
      *
      * Реализовать метод reverse который позволит заменить порядок элементов в списке на противоположный
      */
    @tailrec
    def reverse[T](l: List[T], acc: List[T] = Nil): List[T] = l match {
      case Nil => acc
      case h :: t => reverse(t, h :: acc)
    }

    /**
      *
      * Реализовать метод map для списка который будет применять некую ф-цию к элементам данного списка
      */
    def map[T, B](l: List[T], f: T => B): List[B] = {
      @tailrec
      def loop(acc: List[B], remaining: List[T]): List[B] = remaining match {
        case Nil => reverse(acc)
        case h :: t => loop(f(h) :: acc, t)
      }
      loop(Nil, l)
    }


    /**
      *
      * Реализовать метод filter для списка который будет фильтровать список по некому условию
      */
    def filter[T](l: List[T], f: T => Boolean): List[T] = {
      @tailrec
      def loop(acc: List[T], remaining: List[T]): List[T] = remaining match {
        case Nil => reverse(acc)
        case h :: t => if (f(h)) loop(h :: acc, t) else loop(acc, t)
      }
      loop(Nil, l)
    }



    /**
      *
      * Написать функцию incList котрая будет принимать список Int и возвращать список,
      * где каждый элемент будет увеличен на 1
      */
    def incList(l: List[Int]): List[Int] = {
      @tailrec
      def loop(acc: List[Int], remaining: List[Int]): List[Int] = remaining match {
        case Nil => reverse(acc)
        case h :: t => loop(h + 1 :: acc, t)
      }
      loop(Nil, l)
    }

    /**
      *
      * Написать функцию shoutString котрая будет принимать список String и возвращать список,
      * где к каждому элементу будет добавлен префикс в виде '!'
      */
    def shoutString(l: List[String]): List[String] = {
      @tailrec
      def loop(acc: List[String], remaining: List[String]): List[String] = remaining match {
        case Nil => reverse(acc)
        case h :: t => loop("!" + h :: acc, t)
      }
      loop(Nil, l)
    }

 }