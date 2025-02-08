package ru.otus.module1.futures

import ru.otus.module1.futures.HomeworksUtils.TaskSyntax

import scala.collection.BuildFrom
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object task_futures_sequence {

  /**
   * В данном задании Вам предлагается реализовать функцию fullSequence,
   * похожую на Future.sequence, но в отличии от нее,
   * возвращающую все успешные и не успешные результаты.
   * Возвращаемое тип функции - кортеж из двух списков,
   * в левом хранятся результаты успешных выполнений,
   * в правово результаты неуспешных выполнений.
   * Не допускается использование методов объекта Await и мутабельных переменных var
   */
  /**
   * @param futures список асинхронных задач
   * @return асинхронную задачу с кортежом из двух списков
   */

  def fullSequence[A](futures: List[Future[A]])(implicit ex: ExecutionContext): Future[(List[A], List[Throwable])] = {

    // каждый элемента списка -> Future
    val results: List[Future[Try[A]]] = futures.map(_.transform(Success(_)))

    // Теперь список Futures
    val fList: Future[List[Try[A]]] = Future.sequence(results)

    // Разделяем
    fList.map { results =>
      val successes = results.collect { case Success(value) => value }
      val failures = results.collect { case Failure(exception) => exception }
      (successes, failures)
    }
  }

}
