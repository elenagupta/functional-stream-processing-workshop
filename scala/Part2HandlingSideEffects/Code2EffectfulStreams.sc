import cats.effect.*
import cats.effect.unsafe.implicits.global

val greetingIO = IO.println("Hello!")

import fs2.*

// constructing a stream from an IO
val greetingStream = Stream.eval(greetingIO) //.compile.last
// this won't run but return IO. In order for it to run, we need to .unsafeRunSynch()

val greetThriceStream = greetingStream.repeat.take(3)
// can potentially have a time-out: Stream.eval(greetingIO).repeat.timeout(1.second)
// instead of timeout we can use interruptAfter(1.second)

val greetAndCountIO: IO[Long] = greetThriceStream.compile.count

greetAndCountIO.unsafeRunSync()

/*
If we want to print a range of numbers: Stream.range(0, Int.MaxValue).evalMap takes a function which
takes one of the elements in the range and evaluate
eg. Stream.range(0, Int.MaxValue).evalMap { n => IO.println(s"The number is $n" }

res0.compile.timeout(1.second).count.unsafeRunSynch()
 */

val greetThriceIO: IO[Unit] = greetThriceStream.compile.drain
greetThriceIO.unsafeRunSync()
// drain doesn't accumulate results
// compile is a helper function which helps you choose other functions to do on top of it - coverts stream into an IO

// Stream.exec(IO.sleep(1.second)) has a compile of nothing -> Same result as Stream.eval but output of nothing
/*
exec has type nothing but can still print Mao - it immediately tells it has nothing instead of evaluating Mao
whereas eval has type Unit - use it when we have things we want to propagate down the stream and care about result of IO

eg. Stream.exec.(IO.println("Mao")).take(2) finishes immediately as stream is empty but unsafeRunSynch() will still evaluate Mao
 */
Stream("Mao", "Owl")
  .evalMap(name => IO.println(s"Hi $name!"))
  .head
  .compile
  .drain

val count: Long = Stream(1,2,3).compile.count
val countIO: IO[Long] = Stream(1,2,3).evalMap(IO.println(_)).compile.count

Stream.exec(greetingIO)

Stream(1, 2, 3)
  .evalMap(n => IO.println(s"The number is $n."))
  .compile
  .count
  .unsafeRunSync()

import fs2.io.file.*

Files[IO].list(Path("data")).compile.toList.unsafeRunSync()

Files[IO]
  .readUtf8Lines(Path("data/hard-times.txt"))
  .take(10)
  .compile
  .toList
  .unsafeRunSync()
