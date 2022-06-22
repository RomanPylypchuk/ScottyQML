package quantumroutines.qft

import quantumroutines.qft.PhaseEstimation.{controlBlock, preCircuit}
import scotty.quantum.Circuit
import scotty.quantum.gate.Gate
import scotty.quantum.gate.StandardGate.{PHASE, X}
import utils.Measure.{StateStatsOps, measureTimes}

object PhaseEstimationTest extends App{

  val pre = preCircuit(3)(1)
  val sGen: Int => Gate = n => PHASE((Math.PI / 4) * n, 3)
  val piDiv3Gen: Int => Gate = n => PHASE(2 * (Math.PI / 3) * n, 3)
  val sCircuit = controlBlock(3)(piDiv3Gen)
  val inverseQFT = QFT.inverseQftCircuit(3)
  val phase3WithS = pre combine Circuit(X(3)) combine sCircuit combine inverseQFT

  measureTimes(1000)(phase3WithS).exceptQubits(Set(3)).stats.foreach(println)
}
