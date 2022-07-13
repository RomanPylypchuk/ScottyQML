package utils.algebra

//Interval arithmetic
case class Interval(left: Double, right: Double) {
  def contains(x: Double): Boolean = x >= left && x <= right
  def <(that: Interval): Boolean = this.right < that.left

  //Return a midpoint between ends of this and that Interval
  def midpointBetween(that: Interval): Double =
    if (this < that) (this.right + that.left) / 2.0
    else (that.right + this.left) / 2.0

  override def toString: String = "[" + this.left + ", " + this.right + "]"
}

object Interval{
  implicit class IntervalSyntax(i: Int){
    def in(left: Int, right: Int): Boolean = Interval(left, right).contains(i)
  }
}