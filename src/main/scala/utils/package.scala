import utils.MathOps.CrossOps

package object utils {
  val bits = List(0, 1)

  def allDichotomies(qubitN: Int): List[String] = (bits naryCross qubitN).map(_.mkString)
}
