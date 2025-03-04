package ru.otus.module3

import zio._


object ZIOMain {

  def main(args: Array[String]): Unit = {
    val z1: Task[Unit] = ZIO.attempt(println("Hello"))

    //println(zioRecursion.factorial(10000))
    Unsafe.unsafe { implicit unsafe =>
      zio.Runtime.default.unsafe.run(multipleErrors.app)
    }
  }

}

object ZIOMain2 extends ZIOAppDefault{
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    ZIO.attempt(println("Hello"))
}


