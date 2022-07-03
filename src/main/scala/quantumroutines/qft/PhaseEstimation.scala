package quantumroutines.qft

import scotty.quantum.Circuit
import utils.HTensor

object PhaseEstimation {
  //TODO - express this kind of like `ElementaryCircuit`, with no oracle, but controlled-U^(2^j)

  val preCircuit: Int => Int => Circuit =
    nPhaseQubits =>
      nEigenQubits => {
        val initMinusX: Circuit = Circuit.apply(Circuit.generateRegister(nPhaseQubits + nEigenQubits))
        initMinusX combine Circuit(HTensor(nPhaseQubits): _*)
      }

  def controlBlock: Int => ((Int, Int) => Circuit) => Circuit =
    nPhaseQubits =>
     cUPower2Gen => //Here control index, then j in U^(2^j)
    {
      val controlledUs = (0 until nPhaseQubits).map{cIdx =>
        cUPower2Gen(cIdx, nPhaseQubits - cIdx - 1)
      }
      controlledUs.reduceLeft(_ combine _)
    }

  val phaseEstimate: Int => Int => ((Int, Int) => Circuit) => Circuit = {
    nPhaseQubits =>
      val pre = preCircuit(nPhaseQubits)
      val cBlock = controlBlock(nPhaseQubits)
      val inverseQft = QFT.inverseQftCircuit(nPhaseQubits)
      nEigenQubits =>
       uPowerGen =>
         pre(nEigenQubits) combine cBlock(uPowerGen) combine inverseQft
  }
}
