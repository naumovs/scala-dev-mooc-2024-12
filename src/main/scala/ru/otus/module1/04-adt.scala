package ru.otus.module1

import java.time.LocalDate
import java.time.YearMonth

object adt {

  object tuples {

    /** Tuples ()
     *
     *
      */

    object Foo


    type ProductFooBoolean = (Foo.type, Boolean)

    /** Создать возможные экземпляры с типом ProductFooBoolean
      */

    val v1: ProductFooBoolean = (Foo, true)
    val v2: ProductFooBoolean = (Foo, false)



    /** Реализовать тип Person который будет содержать имя и возраст
      */

    type Person = (String, Int)

    val p: Person = ("Bob", 30)



    /**  Реализовать тип `CreditCard` который может содержать номер (String),
      *  дату окончания (java.time.YearMonth), имя (String), код безопасности (Short)
      */



  }

  object case_classes {

    /** Case classes
      */



    //  Реализовать Person с помощью case класса
    case class Person(name: String, age: Int)


    // Создать экземпляр для Tony Stark 42 года

    val tony = Person("Tony", 42)



    // Создать case class для кредитной карты

  }



  object either {

    /** Sum
      */

    /** Either - это наиболее общий способ хранить один из двух или более кусочков информации в одно время.
      * Также как и кортежи обладает целым рядом полезных методов
      * Иммутабелен
      */


    object Bar


    type BarOrBoolean = Either[Bar.type, Boolean]

    val v1: BarOrBoolean = Left(Bar)
    val v2: BarOrBoolean = Right(true)
    val v3: BarOrBoolean = Right(false)




    /** Реализовать экземпляр типа IntOrString с помощью конструктора Right
      */


    type IntOrString = Either[Int, String]


    object CreditCard
    object WireTransfer
    object Cash

    /** \
      * Реализовать тип PaymentMethod который может быть представлен одной из альтернатив
      */
    type PaymentMethod = Either[CreditCard.type , Either[WireTransfer.type, Cash.type ]]

    val o1: CreditCard.type = ???
    val o2: WireTransfer.type = ???
    val o3: Cash.type = ???

    val v4: PaymentMethod = Left(o1)
    val v5: PaymentMethod = Right(Left(o2))
    val v6: PaymentMethod = Right(Right(o3))




  }

  object sealed_traits {

    /** Также Sum type можно представить в виде sealed trait с набором альтернатив
      */

    sealed trait PaymentMethod
    case object CreditCard extends PaymentMethod
    case object WireTransfer extends PaymentMethod
    case object Cash extends PaymentMethod

    val v1: PaymentMethod = CreditCard
    val v2: PaymentMethod = WireTransfer
    val v3: PaymentMethod = Cash

    val v4: PaymentMethod = ???



  }

  object cards {

    sealed trait Suit                                // масть
    case object Clubs    extends Suit                          // крести
    case object Diamonds extends Suit                          // бубны
    case object Spades   extends Suit                          // пики
    case object Hearts   extends Suit                          // червы
    sealed trait Rank                                // номинал
    case object Two      extends Rank                              // двойка
    case object Three    extends Rank                              // тройка
    case object Four     extends Rank                              // четверка
    case object Five     extends Rank                              // пятерка
    case object Six      extends Rank                              // шестерка
    case object Seven    extends Rank                              // семерка
    case object Eight    extends Rank                              // восьмерка
    case object Nine     extends Rank                              // девятка
    case object Ten      extends Rank                              // десятка
    case object Jack     extends Rank                              // валет
    case object Queen    extends Rank                              // дама
    case object King     extends Rank                              // король
    case object Ace      extends Rank                              // туз
    case class Card(rank: Rank, suit: Suit)                                // карта
    type Deck                                // колода
    type Hand                                // рука
    type Player                              // игрок
    type Game                                // игра
    type PickupCard                          // взять карту

  }

}
