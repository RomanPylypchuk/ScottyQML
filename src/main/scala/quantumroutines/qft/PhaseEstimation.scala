package quantumroutines.qft

import quantumroutines.blocks.CircuitParams.QPEQubitsOld
import scotty.quantum.Circuit
import utils.HTensor

object PhaseEstimation {
  //TODO - express this kind of like `ElementaryCircuit`, with no oracle, but controlled-U^(2^j)

  val initCircuit: Int => Int => Circuit =
    nPhaseQubits =>
      nEigenQubits => {
        Circuit.apply(Circuit.generateRegister(nPhaseQubits + nEigenQubits))
      }

  val controlBlock: Int => ((Int, Int) => Circuit) => Circuit =
    nPhaseQubits =>
      cUPower2Gen => //Here control index, then j in U^(2^j)
      {
        val controlledUs = (0 until nPhaseQubits).map { cIdx =>
          cUPower2Gen(cIdx, nPhaseQubits - cIdx - 1)
        }
        controlledUs.reduceLeft(_ combine _)
      }

  val phaseEstimate: QPEQubitsOld => Circuit => ((Int, Int) => Circuit) => Circuit = {
    qpeQubits =>
      val QPEQubitsOld(nPhaseQubits, nEigenQubits) = qpeQubits
      val init = initCircuit(nPhaseQubits)
      val hadamards = Circuit(HTensor(nPhaseQubits): _*)
      val cBlock = controlBlock(nPhaseQubits)
      val inverseQft = QFT.inverseQftCircuit(nPhaseQubits)
        eigenStatePrep =>
          uPowerGen => {
            init(nEigenQubits) combine eigenStatePrep combine hadamards combine cBlock(uPowerGen) combine inverseQft
          }
  }
}
