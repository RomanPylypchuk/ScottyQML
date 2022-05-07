import scotty.quantum.gate.StandardGate.{H, X}
import utils.MathOps.CrossOps

package object utils {
  val bits = List(0, 1)

  val paddedIntToBinary: Int => Int => String = {
    nQubits =>
     i =>
     val binary = Integer.toBinaryString(i)
     val paddedBinary: String = "0" * (nQubits - binary.length) + binary
     paddedBinary
  }

  def allDichotomies(qubitN: Int): List[String] = (bits naryCross qubitN).map(_.mkString)

  //How to abstract to any gate, e.g. Y, H?
  def placeNOTs(indices: List[Int]): List[X] = indices.map(X)
  def placeHs(indices: List[Int]): List[H] = indices.map(H)
  def HTensor(n: Int): List[H] = placeHs((0 until n).toList)

}
