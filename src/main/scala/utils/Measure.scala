package utils

import scotty.quantum.ExperimentResult.StateStats
import scotty.quantum.{BitRegister, Circuit}
import scotty.simulator.QuantumSimulator
import utils.BitRegisterFactory.{BitRegisterTo, stringBitRegister}
import utils.codec.BiCodec.BiCodecSyntax

object Measure {

  implicit class StateStatsOps(val measurements: StateStats) {

    private def nQubits: Int = measurements.stats.head._1.size

    def forQubits(qubits: Set[Int]): StateStats = {

      val projectBits: BitRegister => BitRegister = {
        bitRegister =>

          val reducedBitValues = bitRegister.values.reverse.zipWithIndex.collect{case (bit, idx) if qubits(idx) => bit}
          BitRegister(reducedBitValues :_*)
      }

      val stats = measurements.stats
      val reducedDichotomies = stats.map{case (fullDichotomy, times) => projectBits(fullDichotomy) -> times}
      val reducedStats: Map[BitRegister, Int] = reducedDichotomies.groupMapReduce{case (dichotomy, _) => dichotomy}{case (_, times) => times}(_ + _)
      StateStats(reducedStats.toList)
    }

    def exceptQubits(qubits: Set[Int]): StateStats = forQubits((0 until nQubits).toSet diff qubits)
  }

  val measureTimes: Int => Circuit => StateStats = {
    trials =>
     circuit => {

      val results = QuantumSimulator()
        .runExperiment(circuit, trialsCount = trials)
        .stateStats
       results
    }
  }

  val measureForAllInputDichotomies: Int => Circuit => Map[String, String] = {
    trials =>
     circuit => {
      val nQubits = circuit.register.size
      val measure = measureTimes(trials)
      val inputBasis: List[String] = allDichotomies(nQubits)
      val prepareStates: List[Circuit] = inputBasis.map(dichotomy => dichotomy.encode[BitRegister].toCircuit)
      val filterStats: StateStats => String = results => results.copy(stats = results.stats.filter { case (_, i) => i != 0 }).toHumanString

      inputBasis.zip(prepareStates).map {
        case (dichotomy, pCircuit) =>
          dichotomy -> filterStats(measure(pCircuit combine circuit))
      }.toMap
    }
  }

}
