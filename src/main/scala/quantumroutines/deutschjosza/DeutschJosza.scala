package quantumroutines.deutschjosza

import scotty.quantum.ExperimentResult.StateStats
import scotty.quantum.gate.StandardGate.{H, X}
import scotty.quantum.{BitRegister, Circuit}
import utils.BitRegisterFactory.BitRegisterFrom
import utils.HTensor
import utils.Measure.{StateStatsOps, measureTimes}

object DeutschJosza {

  def deutschJosza(function: CBOracle): Circuit = {
    val nQubits = function.nOracleQubits + 1
    val left: Circuit = Circuit.apply(Circuit.generateRegister(nQubits), X(nQubits - 1), H(nQubits - 1))
    val oracleHadamards: Circuit = Circuit(HTensor(nQubits - 1) :_*)

    left combine oracleHadamards combine function.oracle combine oracleHadamards
  }

  def runDeutschJosza(function: CBOracle): ConstantOrBalanced = {
    val dj: Circuit = deutschJosza(function)

    /*val qs = QuantumSimulator()

    val trials = (0 until 1000).map(_ => qs.run(dj) match {
      case sp: Superposition =>
        qs.measure(QubitRegister("0"), sp.state)
      case c: Collapsed => c
    })

    val cutNQubits = dj.register.size - 1

    //This is not very nice, but can suffice for now, because proper
    //measurement on a subset of qubits is not implemented in Scotty :(
    val existsNonZeroOutcome = trials.exists(collapsed =>
      !paddedIntToBinary(cutNQubits + 1)(collapsed.index).takeRight(cutNQubits).forall(_ == '0')
    )

    if (existsNonZeroOutcome) Balanced else Constant
    */
    val threshold: Double = 0.95
    val allQubitsStats: StateStats = measureTimes(1000)(dj)
    val neededQubitsStats: StateStats = allQubitsStats.exceptQubits(Set(function.nOracleQubits))
    val statsMap = neededQubitsStats.stats.map{case (bitRegister: BitRegister, times) => bitRegister -> times.toDouble / 1000}.toMap
    val allZerosRatio: Double = statsMap(("0" * function.nOracleQubits).toBitRegister)
    if (allZerosRatio > threshold) Constant else Balanced
  }

}

