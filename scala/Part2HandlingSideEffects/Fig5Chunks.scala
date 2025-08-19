import fs2.*
import cats.effect.*
import aquascape.*

object Fig5Chunks extends WorkshopAquascapeApp {

  override def chunked: Boolean = true

  def stream(using Scape[IO]) = {
    Stream(1, 2, 3)
      .stage("Stream(1, 2, 3)")
      .take(2)
      .stage("take(2)")
      .compile
      .toList
      .compileStage("compile.count")
      .void
  }
}

/*
  with side effects in a stream, we don't want all side effects to get run,
  but only the ones we require ie. 2 in this case not all 3.
  if we were to add evalMap, only 1 side effect would be run per chunk and one unit would be outputted.
 */

/* Stream(1,2,3).debugChinks will print out chunks
  Another operator is evalMapChunk
*/
