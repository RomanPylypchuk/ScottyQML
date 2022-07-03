package quantumroutines.qft

import quantumroutines.qft.PhaseEstimation.{controlBlock, preCircuit}
import scotty.quantum.Circuit
import scotty.quantum.gate.Controlled
import scotty.quantum.gate.StandardGate.{PHASE, X}
import utils.Measure.{StateStatsOps, measureTimes}

object PhaseEstimationTest extends App{

  //Controlled(cIdx, uPower2Gen(nPhaseQubits - cIdx - 1))
  val pre = preCircuit(3)(1)

  val sGen: (Int, Int) => Circuit =
    (ci, j) => Circuit(Controlled(ci, PHASE((Math.PI / 4) * math.pow(2, j).toInt, 3)))

  val piDiv3Gen: (Int, Int) => Circuit =
    (ci, j) => Circuit(Controlled(ci, PHASE(2 * (Math.PI / 3) * math.pow(2, j).toInt, 3)))

  val sCircuit = controlBlock(3)(piDiv3Gen)
  val inverseQFT = QFT.inverseQftCircuit(3)
  val phase3WithS = pre combine Circuit(X(3)) combine sCircuit combine inverseQFT

  measureTimes(1000)(phase3WithS).exceptQubits(Set(3)).stats.foreach(println)
}
