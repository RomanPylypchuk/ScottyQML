package utils

import spire.math.Rational
import utils.MathOps.rationalToContinuedFractions
import utils.codec.BiCodec.BiCodecSyntax

object MathOpsTest extends App{

  val rational = Rational(31,13)
  val contFractions: List[Long] = rational.decodeE[Option[Int], List[Long]](None)
  val rationalBack: Rational = contFractions.encodeE[Option[Int], Rational](None)

  println(contFractions)
  assert(rational == rationalBack)

  (1 to contFractions.length)
    .map(nConvergent => contFractions.encodeE[Option[Int], Rational](Some(nConvergent)))
    .foreach(println)

}
