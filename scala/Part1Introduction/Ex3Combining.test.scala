import cats.effect.*
import munit.*
import fs2.*
import cats.syntax.all.*
import cats.data.*

class Ex3Combining extends CatsEffectSuite {

  val kittens = Stream("Mao", "Popcorn")

  val food = Stream("Tuna", "Duck", "Chicken")

  test("append") {
    val result: Stream[Pure, String] = kittens.append(food)
    assertEquals(
      result.compile.toList,
      List("Mao", "Popcorn", "Tuna", "Duck", "Chicken")
    )
  }

  test("zip") {
    val result: Stream[Pure, (String, String)] = kittens.zip(food)
    assertEquals(
      result.compile.toList,
      List(("Mao", "Tuna"), ("Popcorn", "Duck"))
    )
  }

  test("interleave") {
    val result: Stream[Pure, String] = kittens.interleave(food)
    assertEquals(result.compile.toList, List("Mao", "Tuna", "Popcorn", "Duck"))
  }

  test("flatMap") {
    val result: Stream[Pure, String] = kittens.flatMap( oneCat => food.map(oneFood => s"$oneCat-$oneFood"))
    assertEquals(
      result.compile.toList,
      List(
        "Mao-Tuna",
        "Mao-Duck",
        "Mao-Chicken",
        "Popcorn-Tuna",
        "Popcorn-Duck",
        "Popcorn-Chicken"
      )
    )
  }
}
