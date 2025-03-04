package ru.otus.module3

import zio.IO
import zio.ZIO
import zio.Cause.{Both}


sealed trait Error extends Product
case object E1 extends Error
case object E2 extends Error

object multipleErrors{
    val z1: IO[E1.type, Int] = ZIO.fail(E1)

    val z2: IO[E2.type, Int] = ZIO.fail(E2)

    lazy val result = z1 zipPar z2

    lazy val app = result.tapErrorCause{
        case Both(left, Both(l2, r2)) =>
            ZIO.attempt(println(l2.failureOption)) zipRight
                ZIO.attempt(println(r2.failureOption))
    }
}
