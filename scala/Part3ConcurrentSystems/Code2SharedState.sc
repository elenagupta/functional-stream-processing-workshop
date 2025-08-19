import fs2.*
import scala.concurrent.duration.*
import cats.effect.IO
import cats.effect.Ref
import cats.effect.unsafe.implicits.global

/** A ref can be used to safely share state */
val counter = Ref.of[IO, Int](0).unsafeRunSync()
/* A Ref is like a box which contains a value we can then change
 we can access the Ref of streams which are running concurrently
 right now Ref has a value of 0
*/

/** It has get, set and update functions */
counter.get.unsafeRunSync() //this is an IO which gets the value inside the Ref

/* Could also do counter.set(42) and this means that counter.get.unsafeRunSynch() is 42
NOTE: We need unsafeRunSynch() in order for it to compile and get the new value set to it
 */



counter
  .set(42)
  .flatMap(_ => counter.get)
  .unsafeRunSync()

counter
  .update(_ + 42)
  .flatMap(_ => counter.get)
  .unsafeRunSync()

/** It can be updated as part of a stream */
val incrementEverySecond =
  Stream.eval(counter.update(_ + 1)).repeat.spaced(1.second)

incrementEverySecond
  .take(3)
  .compile
  .drain
  .flatMap(_ => counter.get)
  .unsafeRunSync()

/** It can be updated and accessed concurrently */
val printTwicePerSecond =
  Stream.eval(counter.get.flatMap(IO.println)).repeat.spaced(500.millis)

incrementEverySecond
  .take(3)
  .concurrently(printTwicePerSecond)
  .compile
  .drain
  .unsafeRunSync()

/* Can do Stream(incrementEverySecond, printTwicePerSecond).parJoinUnbounded
    .compile.drain.timeout(5.seconds)
The output will be a combination of the counter incrementing by 1 and being printed out twice the same number
 */

import fs2.concurrent.*

SignallingRef
  .of[IO, Int](0)
  .flatMap { counter =>
    val incrementEverySecond =
      Stream.repeatEval(counter.update(_ + 1)).spaced(1.second)

    val printOnChange = counter.discrete.evalMap(IO.println)
    incrementEverySecond
      .take(3)
      .concurrently(printOnChange)
      .compile
      .drain
  }
  .unsafeRunSync()

SignallingRef
  .of[IO, Int](0)
  .flatMap { counter =>
    val incrementEverySecond =
      Stream.repeatEval(counter.update(_ + 1)).spaced(1.second)
    val printTwicePerSecond =
      counter.continuous.spaced(500.millis).evalMap(IO.println)

    incrementEverySecond
      .take(3)
      .concurrently(
        printTwicePerSecond
      )
      .compile
      .drain
  }
  .unsafeRunSync()

/* .discrete gives a stream of Integers and gives the discrete values of Ref
discrete will only output an element when the value changes
as well, there is a .continuous function which will keep printing out the numbers
 */
