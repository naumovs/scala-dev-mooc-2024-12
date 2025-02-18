package ru.otus.module1.DataCollection2

import scala.util.Try

class MonadParser[T, Src](private val p: Src => (T, Src)):

  def flatMap[M](f: T => MonadParser[M, Src]): MonadParser[M, Src] =
    MonadParser { src =>
      val (word, rest) = p(src)
      val mn = f(word)
      val res = mn.p(rest)
      res
    }

  def map[M](f: T => M): MonadParser[M, Src] =
    MonadParser { src =>
      val (word, rest) = p(src)
      (f(word), rest)
    }

  def parse(src: Src): T = p(src)._1

end MonadParser

class ParserWithGivenParam(using splitter: String):
  def stringField: MonadParser[String, String] = MonadParser[String, String] { str =>
    val idx = str.indexOf(splitter)
    if idx > -1 then
      (str.substring(0, idx), str.substring(idx + splitter.length))
    else
      (str, "")
  }

  def intField: MonadParser[Int, String] = stringField.map(_.toInt)
  def booleanField: MonadParser[Boolean, String] = stringField.map(_.toBoolean)

end ParserWithGivenParam

object MonadParser:
  def apply[T, Src](f: Src => (T, Src)) = new MonadParser[T, Src](f)

object TestExecutor:
  @main
  def main(): Unit =

    given splitter: String = ";"

    val str = "1997;Ford;Passat;true\n1901;Ford;T;false"

    case class Car(year: Int, mark: String, model: String, canDrive: Boolean)
    // override def toString: String = s"Car($year, $mark, $model, $canDrive)"

    val parser = new ParserWithGivenParam

    val carParser = for
      year <- parser.intField
      mark <- parser.stringField
      model <- parser.stringField
      canDrive <- parser.booleanField
    yield Car(year, mark, model, canDrive)

    val result: List[Try[Car]] = str.split("\n").map(line => Try(carParser.parse(line))).toList
    println(result)

end TestExecutor
