package ru.otus

import ru.otus.module1.{hof, type_system}

object Main {

  def main(args: Array[String]): Unit = {
    val dumb = hof.logRunningTime(hof.dumb)
    dumb("Hello")
  }
}
