package utils

import cats.data.Reader
import spire.math.Rational
import utils.algebra.Isomorphism.<=>
import utils.codec.BiCodec

object MathOps {

  //Inverse isomap
  def inverseMap[K, V](directMap: Map[K, V]): Map[V, K] =
    directMap.map { case (k, v) => (v, k) }

  //Filtered Cartesian product
  def crossJoin[T](list: List[List[T]],
                   combinator: (T, List[T]) => Option[List[T]] = (head: T, tail: List[T]) => Some(head :: tail)): List[List[T]] = {
    list match {
      case Nil => Nil
      case x :: Nil => x map (List(_))
      case x :: xs =>
        val xsJoin = crossJoin(xs, combinator)
        for {
          i <- x
          j <- xsJoin
          combined <- combinator(i, j)
        } yield combined
    }
  }

  implicit class CrossOps[T](items: List[T]) {
    def naryCross(n: Int): List[List[T]] = crossJoin(List.fill(n)(items))
  }

  def zip[A, B](pair: Tuple2[Array[A], Array[B]]): Array[(A, B)] = {
    val (xs, ys) = pair
    xs.zip(ys)
  }

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
