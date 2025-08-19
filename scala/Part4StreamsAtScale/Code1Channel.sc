import fs2.*
import fs2.concurrent.*
import cats.effect.*
import cats.effect.unsafe.implicits.global
import scala.concurrent.duration.*

Channel.unbounded[IO, String]
/* channel of Streams - this is unbounded and at any point anyone can put anything inside it.
    so need to have a time out of the channels to tell that the channel has ended.
 */

/* Channel.send("Chocolate").unsafeRunSynch()
  channel.stream..evalMap( text => IO.println(text) ).compile.drain.timeout(4.seconds).unsafeRunSynch()
  OUT: Chocolate and then time-out after 4 seconds
 */

/* 
Can say: channel.close.unsafeRunSynch() <- this closes the channel
So, channel.close, then channel.stream..evalMap( text => IO.println(text) ).compile.toList.unsafeRunSynch()
OUT: List[Unit] = List()
 */

/*
Bounded channels:
boundedChanel = channel.bounded[IO, String](2).unsafeRunSynch()
boundedChanel.send(Chocolate).unsafeRunSynch()
boundedChanel.send(Chocolate-2).unsafeRunSynch()
boundedChanel.send("Chocolate-3").timeout(5.seconds).unsafeRunSynch() <- this times out as the chanel already has 2 chocolates so it's full
 TRY: boundedChanel.trySend("Chocolate-3").timeout(5.seconds).unsafeRunSynch()
 */

/* Queue vs Channel: Queue is a buffer and can see what's inside, channel is more for communicating between streams */

Stream
  .iterate(0)(_ + 1)
  .evalMap(i => IO.println(s"Producing $i"))
  .metered(1.second)

Channel.unbounded[IO, Int].unsafeRunSync()

Channel
  .unbounded[IO, Int]
  .flatMap { channel =>
    val producer = Stream
      .iterate(0)(_ + 1)
      .evalTap(i => IO.println(s"Producing $i"))
      .metered(1.second)
      .through(channel.sendAll)
    val consumer = channel.stream.evalMap(i => IO.println(s"Consuming $i"))
    consumer.concurrently(producer).compile.drain
  }
  .timeout(3.second)
  .unsafeRunSync()

Channel
  .unbounded[IO, Int]
  .flatMap { channel =>
    val producer = Stream
      .iterate(0)(_ + 1)
      .evalTap(i => IO.println(s"Producing $i"))
      .metered(1.second)
      .through(channel.sendAll)
    val consumer = channel.stream
      .evalMap(i => IO.println(s"Consuming $i"))
      .metered(2.seconds)
    consumer.concurrently(producer).compile.drain
  }
  .timeout(10.second)
  .unsafeRunSync()

Channel
  .bounded[IO, Int](2)
  .flatMap { channel =>
    Stream
      .range(0, Int.MaxValue)
      .debug()
      .through(channel.sendAll)
      .compile
      .drain
  }
  .timeout(10.second)
  .unsafeRunSync()

Channel
  .bounded[IO, Int](2)
  .flatMap { channel =>
    val producer = Stream
      .iterate(0)(_ + 1)
      .evalTap(i => IO.println(s"Producing $i"))
      .metered(1.second)
      .through(channel.sendAll)
    val consumer = channel.stream
      .evalMap(i => IO.println(s"Consuming $i"))
      .metered(2.seconds)
    consumer.concurrently(producer).compile.drain
  }
  .timeout(20.second)
  .unsafeRunSync()

Channel
  .bounded[IO, Int](2)
  .flatMap { channel =>
    val producer = Stream
      .iterate(0)(_ + 1)
      .evalTap(i => IO.println(s"Producing $i"))
      .metered(1.second)
      .through(channel.sendAll)
    val consumer = channel.stream
      .evalMap(i => IO.println(s"Consuming $i"))
      .metered(2.seconds)
    consumer.concurrently(producer).compile.drain
  }
  .timeout(20.second)
  .unsafeRunSync()

Channel
  .bounded[IO, Int](2)
  .flatMap { channel =>
    val producer = Stream
      .iterate(0)(_ + 1)
      .take(3)
      .evalTap(i => IO.println(s"Producing $i"))
      .metered(1.second)
      .through(channel.sendAll)
    val consumer = channel.stream
      .evalMap(i => IO.println(s"Consuming $i"))
      .metered(2.seconds)
    consumer.concurrently(producer).compile.drain
  }
  .timeout(20.second)
  .unsafeRunSync()
