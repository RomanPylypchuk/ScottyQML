package utils.algebra

import cats.data.Reader
import spire.math.Rational
import utils.algebra.Isomorphism.<=>
import utils.codec.BiCodec

import scala.annotation.tailrec
import scala.math.Integral.Implicits._

object NumberTheoryRoutines {

  final case class Var(x: Long)
  final case class Expr(lhs: Var, rhs: Map[Var, Long])

  @tailrec
  def gcd(x: Long, y: Long, acc: List[Expr] = Nil): (Long, List[Expr]) = {
    val (xl, xs) = (x max y, x min y)
    val (q, r) = xl /% xs
    if (r == 0) (xs, acc) else {
      val expr = Expr(Var(r), Map(Var(xl) -> 1, Var(xs) -> -q))
      gcd(xs, r, expr :: acc)
    }
  }

  def lcm(x: Long, y: Long): Long = (x * y) / gcd(x, y)._1

  implicit val rationalToContinuedFractions: Reader[Option[Int], BiCodec[List[Long], Rational]] = Reader(
    nConvergent =>

      BiCodec(
        new (List[Long] <=> Rational) {

          def to: List[Long] => Rational = {
            fCoefs =>
              val modCoefs: List[Long] = nConvergent.fold(fCoefs)(n => fCoefs.take(n))
              val last :: restReverse = modCoefs.reverse
              restReverse.foldLeft(Rational(last, 1)) { case (acc, coef) => acc.inverse + coef }
          }

          def from: Rational => List[Long] = {
            x =>
              def rationalToContinuedFractionsRecur(x: Rational): List[Long] = {
                if (x.isWhole) x.longValue :: Nil
                else {
                  val remainder: Rational = x - x.longValue
                  if (remainder.numerator == 1)
                    x.longValue :: remainder.denominator.longValue :: Nil
                  else
                    x.longValue :: rationalToContinuedFractionsRecur(remainder.inverse)
                }
              }
              rationalToContinuedFractionsRecur(x)
          }
        }
      ))
}
