import munit.*
import fs2.*

class Ex1WhatAreStreams extends CatsEffectSuite {

  val numbers: Stream[Pure, Int] = Stream.range(0, Int.MaxValue)

  //if you want only one test to be run, you can do: test("get the first two numbers".only)
  test("get the first two numbers") {
    val result: List[Int] = numbers
    .debug(n => s"The number is $n").take(2).toList
    assertEquals(result, List(0, 1))
  }

  test("get the seventh to the ninth number") {
    val result: List[Int] = numbers.drop(6).take(3).toList
    assertEquals(result, List(6, 7, 8))
  }

  test("get odd numbers less than six") {
    val result: List[Int] = numbers.filter(x => x % 2 == 1).take(3).toList
    assertEquals(result, List(1, 3, 5))
  }
  // or numbers.filter(x => x % 2 == 1).takeWhile(_ < 6)

  test("get the 1000th number") {
    val result: Int = numbers.drop(1000).take(1).toList.head
    assertEquals(result, 1000)
  }
  // or numbers.drop(1000).head.compile.last.gets

  test("get the sum of the first 10 numbers") {
    val result = numbers.take(10).toList.foldLeft(0)(_ + _)
    assertEquals(result, 45)
  }
  // or numbers.take(10).compile.foldMonoid <- Moniod is something which adds
  // or just fold(0)(_ + _)
  // or .reduce(_ + _).compile.last.getOrElse(0)

}
