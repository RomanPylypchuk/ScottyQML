package utils

import scotty.quantum.Circuit
import scotty.quantum.ExperimentResult.StateStats
import scotty.simulator.QuantumSimulator
import utils.BitRegisterFactory.{BitRegisterFrom, BitRegisterTo}

object Measure {

  val measureTimes: Int => Circuit => StateStats = {
    trials =>
     circuit => {

      val results = QuantumSimulator()
        .runExperiment(circuit, trialsCount = trials)
        .stateStats

       results
      //results.copy(stats = results.stats.filter { case (_, i) => i != 0 }).toHumanString
    }
  }

  val measureForAllInputDichotomies: Int => Circuit => Map[String, String] = {
    trials =>
     circuit => {
      val nQubits = circuit.register.size
      val measure = measureTimes(trials)
      val inputBasis: List[String] = allDichotomies(nQubits)
      val prepareStates: List[Circuit] = inputBasis.map(dichotomy => dichotomy.toBitRegister.toCircuit)
      val filterStats: StateStats => String = results => results.copy(stats = results.stats.filter { case (_, i) => i != 0 }).toHumanString

      inputBasis.zip(prepareStates).map {
        case (dichotomy, pCircuit) =>
          dichotomy -> filterStats(measure(pCircuit combine circuit))
      }.toMap
    }
  }

}
