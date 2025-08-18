import munit.*
import fs2.*

class Ex2IncrementalPipelines extends CatsEffectSuite {

  val userIds: Stream[Pure, String] =
    Stream.range(0, Int.MaxValue).map(id => s"u$id")

  val oneAndFortyTwo: Stream[Pure, String] =
    Stream("u42", "u1", "u42").repeatN(100)

//  def mostCommon(k: Int, ids: Stream[Pure, String]): String = {
//    // Replace this implementation with the Misra Gries summary.
//    val initialCounts: Map[String, Int] = Map.empty
//    val finalCounts = ids
//      .fold(initialCounts) { (counts, id) =>
//        val previousCount = counts.getOrElse(id, 0)
//        counts + ((id, previousCount + 1))
//      }
//      .compile
//      .last
//      .getOrElse(initialCounts)
//    val (mostCommonId, _) = finalCounts.maxBy { case (_, count) => count }
//    mostCommonId
//  }

/*  algorithm misra -gries:
  input: A positive integer k
          A finite sequence s taking values in the range 1, 2,..., m
  output: An associative array A with frequency estimates
  for each item in s

        A := new(empty) associative array
  while s is not empty :
    take a value i from s
    if i is in keys (A):
      A[i] := A[i] + 1 //increment the count if element exists in map
    else if | keys (A) | < k -1:
      A[i] := 1
    else:
    for each K in keys (A):
          A[K] := A[K] - 1 
        if A[K]
    = 0:
    remove K from keys (A) //this is removing an element an element if is count is 0 to add different values in instead
  return A
 most common element in map is the most common value in stream
*/
  
  def mostCommon(k: Int, ids: Stream[Pure, String]): String = {
    val initialCounts: Map[String, Int] = Map.empty
    val finalCounts = ids
      .fold(initialCounts) { (counts, id) =>
        val previousCount = counts.getOrElse(id, 0)
        counts + ((id, previousCount + 1))
      }
      .compile
      .last
      .getOrElse(initialCounts)
    val (mostCommonId, _) = finalCounts.maxBy { case (_, count) => count }
    mostCommonId
  }

  test("The answer is forty two") {
    assertEquals(mostCommon(k = 100, oneAndFortyTwo), "u42")
  }

  test("The mostCommon function can process a large volume of ids") {
    val mostlyFortyTwo = Stream("u42").repeatN(100) ++ userIds
    assert(clue(mostCommon(k = 100, mostlyFortyTwo)) == "u42")
  }

  test("the most common element among mostly forty twos is forty two") {
    // Construct your own test stream
    val testData: Stream[Pure, String] =
      userIds.interleave(Stream("u42").repeat).take(Int.MaxValue)
    assertEquals(mostCommon(k = 100, testData), "u42")
  }
}
