import utils.MathOps.CrossOps

package object utils {
  val bits: List[Int] = List(0, 1)

  val logBase: Int => Double => Double =
    n =>
      x => math.log(x) / math.log(n)

  val padLeft: Int => String => String = {
    n =>
      str =>
        "0" * (n - str.length) + str
  }

  val paddedIntToBinary: Int => Int => String =
    nQubits =>
     padLeft(nQubits) compose Integer.toBinaryString

  def allDichotomies(qubitN: Int): List[String] = (bits naryCross qubitN).map(_.mkString)
}
