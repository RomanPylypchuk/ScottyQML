package utils

import scotty.quantum.Circuit
import scotty.quantum.gate.StandardGate.{CNOT, H, X}
import scotty.quantum.gate._

object GateUtils {

  val placeSingleQubitGate: (Int => TargetGate) => List[Int] => List[TargetGate] =
    gateGen => indices => indices.map(gateGen)

  val placeHs: List[Int] => List[TargetGate] = placeSingleQubitGate(H.apply)
  val placeNOTs: List[Int] => List[TargetGate] = placeSingleQubitGate(X.apply)
  val placeCNOTs: Map[Int, List[Int]] => List[CNOT] = {
    controlsTargets =>
      val cNOTs = for {
        (ci, tis) <- controlsTargets
        ti <- tis
      } yield CNOT(ci, ti)
      cNOTs.toList
  }
  val singlePlaceCNOTs: Map[Int, Int] => List[CNOT] = {
    controlTargetMap =>
      placeCNOTs(controlTargetMap.map { case (ci, ti) => ci -> List(ti) })
  }

  def HTensor(n: Int): List[TargetGate] = placeHs((0 until n).toList)
  def XTensor(n: Int): List[TargetGate] = placeNOTs((0 until n).toList)

  val inverseGate: PartialFunction[Gate, Gate] = {
    case tg: TargetGate => Dagger(tg)
    case sg: SwapGate => sg
    case cg: ControlGate =>
      val c = Controlled(cg.controlIndex, inverseGate(cg.target))
      c : ControlGate
  }

  implicit class InverseCircuit(val circuit: Circuit) {
    def dagger: Circuit = {
       val inverseGates = circuit.gates.map(inverseGate)
       Circuit(inverseGates.reverse :_*)
    }
  }
}
