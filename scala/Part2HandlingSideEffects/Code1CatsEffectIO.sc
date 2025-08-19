val greeting: Unit = println("Hello!")

import cats.effect.IO

// this suspends the side effect so later on we can evaluate it
val greetingIO0 = IO(println("Hello!"))

val greetingIO = IO.println("Hello!")

val greetTwice = {
  greetingIO.flatMap(_ => greetingIO)
}
// composed with an IO block which gives another IO block

import cats.effect.unsafe.implicits.global
greetTwice.unsafeRunSync()

greetingIO.unsafeRunSync() // this will print Hello!