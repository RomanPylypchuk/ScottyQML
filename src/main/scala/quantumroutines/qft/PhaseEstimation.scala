package quantumroutines.qft

import scotty.quantum.Circuit
import scotty.quantum.gate.{Controlled, Gate}
import utils.HTensor

object PhaseEstimation {
  //TODO - express this kind of like `ElementaryCircuit`, with no oracle, but controlled-U^(2^j)

  val preCircuit: Int => Int => Circuit =
    nPhaseQubits =>
      nEigenQubits => {
        val initMinusX: Circuit = Circuit.apply(Circuit.generateRegister(nPhaseQubits + nEigenQubits))
        initMinusX combine Circuit(HTensor(nPhaseQubits): _*)
      }

  def controlBlock: Int => (Int => Gate) => Circuit =
    nPhaseQubits =>
     uPowerGen =>
    {
      val controlledUs = (0 until nPhaseQubits).map{cIdx =>
        Controlled(cIdx, uPowerGen(math.pow(2, nPhaseQubits - cIdx - 1).toInt))
      }
      Circuit(controlledUs :_*)
    }


}
