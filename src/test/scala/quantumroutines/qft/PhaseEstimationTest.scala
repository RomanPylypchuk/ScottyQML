package quantumroutines.qft

import quantumroutines.blocks.CircuitParams.QPEQubitsOld
import quantumroutines.qft.PhaseEstimation.{initCircuit, phaseEstimate}
import scotty.quantum.Circuit
import scotty.quantum.gate.Controlled
import scotty.quantum.gate.StandardGate.{PHASE, X}
import utils.Measure.{StateStatsOps, measureTimes}

object PhaseEstimationTest extends App{

  //Controlled(cIdx, uPower2Gen(nPhaseQubits - cIdx - 1))
  val pre = initCircuit(3)(1)

  val sGen: (Int, Int) => Circuit =
    (ci, j) => Circuit(Controlled(ci, PHASE((Math.PI / 4) * math.pow(2, j).toInt, 3)))

  val piDiv3Gen: (Int, Int) => Circuit =
    (ci, j) => Circuit(Controlled(ci, PHASE(2 * (Math.PI / 3) * math.pow(2, j).toInt, 3)))

  val piDivThreeEstimate: Circuit = phaseEstimate(QPEQubitsOld(3,1))(Circuit(X(3)))(piDiv3Gen)

  //phase3WithS
  measureTimes(1000)(piDivThreeEstimate).exceptQubits(Set(3)).stats.foreach(println)
}
