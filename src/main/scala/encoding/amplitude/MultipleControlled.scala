package encoding.amplitude

import scotty.quantum.gate.StandardGate.X
import scotty.quantum.gate._
import scotty.quantum.{Bit, BitRegister, Zero}
import utils.placeNOTs

object MultipleControlled {

  def multipleControlled(targetGate: TargetGate, indices: Int*): Gate =
    indices.tail.foldLeft(Controlled(indices.head, targetGate)) {
      case (prevControlled, idx) => Controlled(idx, prevControlled)
    }

  val controlGateControlIndices: Gate => List[Int] = {
    case c: ControlGate => c.controlIndex :: controlGateControlIndices(c.target)
    case _: TargetGate => Nil
  }

  val inverseControlled: ControlGate => CompositeGate =
    controlGate => {
      val cIndices: List[Int] = controlGateControlIndices(controlGate)
      val nots: Array[Gate] = placeNOTs(cIndices).toArray
      val gates: Array[Gate] = (nots :+ controlGate) ++ nots
      CompositeGate(gates: _*)
    }

  val controlledConfigurationGate: Map[Int, Bit] => TargetGate => CompositeGate =
    controlMap =>
     targetGate =>
        {
        val zeroIndices = controlMap.collect{case (zIdx, Zero(_)) => zIdx}
        val justMultipleControlled = multipleControlled(targetGate, controlMap.keys.toList :_*)
        val nots: Array[Gate] = zeroIndices.map(zIdx => X(zIdx)).toArray
        val gates: Array[Gate] = (nots :+ justMultipleControlled) ++ nots
        CompositeGate(gates: _*)
      }

  val dichotomyToControlMap: BitRegister => Map[Int, Bit] = dichotomy =>
    dichotomy.values.zipWithIndex.map{case (bit, idx) => (idx, bit)}.toMap
}
