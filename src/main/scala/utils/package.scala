import scotty.quantum.gate.StandardGate.{CNOT, H, X}
import scotty.quantum.gate.TargetGate
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

  val placeSingleQubitGate : (Int => TargetGate) => List[Int] => List[TargetGate] =
    gateGen => indices => indices.map(gateGen)

  val placeHs: List[Int] => List[TargetGate] = placeSingleQubitGate(H.apply)
  val placeNOTs: List[Int] => List[TargetGate] = placeSingleQubitGate(X.apply)
  val placeCNOTs: Map[Int, List[Int]] => List[CNOT] = {
    controlsTargets =>
    val cNOTs = for{
      (ci, tis) <- controlsTargets
      ti <- tis
    } yield CNOT(ci, ti)
    cNOTs.toList
  }
  val singlePlaceCNOTs: Map[Int, Int] => List[CNOT] = {
    controlTargetMap =>
    placeCNOTs(controlTargetMap.map{case (ci, ti) => ci -> List(ti)})
  }

  def HTensor(n: Int): List[TargetGate] = placeHs((0 until n).toList)
}
