package ru.otus.module1

object pattern_matching{
     // Pattern matching

  /**
   * Матчинг на типы
   */

   val i: Any = 10

   i match {
     case v: Int => println("Int")
     case v: String => println("String")
     case v: List[String] => println("List[String]")
     case v: List[Int] => println("List[Int]")
     case _ => println("Unknown type")
   }







  /**
   * Структурный матчинг
   */




  sealed trait Animal{

    def whoIam: String = this match {
      case Dog(n, _) => s"I,m dog - ${n}"

      case Cat(n, _) => s"I,m cat - ${n}"
    }


  }


  case class Dog(name: String, age: Int) extends Animal
  class Cat(val name: String, val age: Int) extends Animal

  object Cat{
    def unapply(c: Cat): Option[(String, Int)] = ???
  }

  val Dog(n, ag) = Dog("bobik", 2)


  /**
   * Матчинг на литерал
   */

  val animal: Animal = ???

  animal match {
    case Dog("Bim", age) => ???
    case Dog(n, age) => ???
    case Cat(name, age) => ???
  }




  val Bim = "Bim"


  /**
   * Матчинг на константу
   */

  animal match {
    case Dog(bim, age) => ???
    case Cat(name, age) => ???
  }



  /**
   * Матчинг с условием (гарды)
   */

  animal match {
    case Dog(name, age) if age > 5 => ???
    case Cat(name, age) => ???
  }


  /**
   * "as" паттерн
   */

  def treatCat(cat: Cat) = ???
  def treatDog(dog: Dog) = ???



  /**
   * используя паттерн матчинг напечатать имя и возраст
   */

  def treatAnimal(a: Animal) = a match {
    case d : Dog =>
      treatDog(d)
    case c : Cat =>
      treatCat(c)
  }



  final case class Employee(name: String, address: Address)
  final case class Address(val street: String, val number: Int)


  val alex = Employee("Alex", Address("XXX", 221))



  case class Person(name: String, age: Int)


  val tony = Person("Tony", 42)

  /**
   * воспользовавшись паттерн матчингом напечатать номер из поля адрес
   */

   alex match {
     case Employee(_, Address(_, number)) => println(number)
   }




  /**
   * Паттерн матчинг может содержать литералы.
   * Реализовать паттерн матчинг на alex с двумя кейсами.
   * 1. Имя должно соотвествовать Alex
   * 2. Все остальные
   */




  /**
   * Паттерны могут содержать условия. В этом случае case сработает,
   * если и паттерн совпал и условие true.
   * Условия в паттерн матчинге называются гардами.
   */



  /**
   * Реализовать паттерн матчинг на alex с двумя кейсами.
   * 1. Имя должно начинаться с A
   * 2. Все остальные
   */


  /**
   *
   * Мы можем поместить кусок паттерна в переменную использую `as` паттерн,
   * x @ ..., где x это любая переменная.
   * Это переменная может использоваться, как в условии,
   * так и внутри кейса
   */

    trait PaymentMethod
    case object Card extends PaymentMethod
    case object WireTransfer extends PaymentMethod
    case object Cash extends PaymentMethod

    case class Order(paymentMethod: PaymentMethod)

    lazy val order: Order = ???

    lazy val pm: PaymentMethod = ???


    def checkByCard(o: Order) = ???

    def checkOther(o: Order) = ???



  /**
   * Мы можем использовать вертикальную черту `|` для матчинга на альтернативы
   */

   sealed trait A
   case class B(v: Int) extends A
   case class C(v: Int) extends A
   case class D(v: Int) extends A

   val a: A = ???

  a match {
    case B(_) | C(_) => ???
    case D(_) => ???
  }


  val divide: PartialFunction[(Int, Int), Int] = new PartialFunction[(Int, Int), Int] {
    override def isDefinedAt(x: (Int, Int)): Boolean = x._2 != 0

    override def apply(v1: (Int, Int)): Int = v1._1 / v1._2
  }

}