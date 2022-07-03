package utils.algebra.precision

case class Precision(epsilon: Double)

object Precision{
  implicit class AlmostEquals(x: Double){
    def ~=(y: Double)(implicit p: Precision): Boolean = (x - y).abs <= p.epsilon
  }
}