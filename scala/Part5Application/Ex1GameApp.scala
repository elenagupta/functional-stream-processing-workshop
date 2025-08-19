import cats.effect.*
import fs2.*
import cats.effect.*
import doodle.java2d.*
import fs2.concurrent.*
import scala.concurrent.duration.*
import cats.syntax.all.*
import doodle.interact.*
import doodle.interact.syntax.all.*
import doodle.core.*

trait GameApp[S, C] extends IOApp.Simple {

  def game: IO[Game[S, C]]
  //if our user was a game developer we might want side effect stuff

  // Implement this function
  def run: IO[Unit] = {
    game.flatMap{ gameValue =>

      val frame = Frame.default.withSize(600,600).withBackground(Color.paleGreen)

      SignallingRef.of[IO,S](gameValue.init).flatMap{ gameStateRef =>
        val renderLoop = gameStateRef.discrete.map(gameValue.render) //steam of pics
        
        val eventLoop = Stream.eval(IO.readLine).repeat
          .mapFilter(gameValue.input) //filter out all the Nones
          .map{ command => 
            val actionStream = gameValue.action(command, gameStateRef)
            actionStream
          }.parJoinUnbounded
        
        val simulationLoop = gameValue.simulation(gameStateRef) //stream of numbers
        
        Stream(renderLoop, simulationLoop, eventLoop)
          .parJoinUnbounded
          .animateToIO(frame)
      }
    }
  }
}
