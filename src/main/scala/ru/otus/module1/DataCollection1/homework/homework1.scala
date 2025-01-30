package ru.otus.module1.DataCollection1

import scala.util.Random

class BallsExperiment {

  val drum: List[Int] = List(1, 1, 1, 0, 0, 0)

  def isFirstBlackSecondWhite: Boolean = {
    val shuffled = Random.shuffle(drum)
    val firstBall = shuffled.head
    val secondBall = Random.shuffle(shuffled.tail).head
    firstBall == 1 || secondBall == 1 //Найти вероятность появления белого шара
  }
}

object BallsTest {
  def main(args: Array[String]): Unit = {
    val count = 10000
    val listOfExperiments: List[BallsExperiment] = List.fill(count)(new BallsExperiment)
    val countOfExperiments = listOfExperiments.map(_.isFirstBlackSecondWhite)
    val countOfPositiveExperiments: Float = countOfExperiments.count(_ == true)
    println(countOfPositiveExperiments / count)
  }
}
