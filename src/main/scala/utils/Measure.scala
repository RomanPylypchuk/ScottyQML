package utils

import scotty.quantum.Circuit
import scotty.simulator.QuantumSimulator
import utils.BitRegisterFactory.{BitRegisterFrom, BitRegisterTo}

object Measure {

  val measureTimes: Int => Circuit => String = {
    trials =>
     circuit => {

      val results = QuantumSimulator()
        .runExperiment(circuit, trialsCount = trials)
        .stateStats

      results.copy(stats = results.stats.filter { case (_, i) => i != 0 }).toHumanString
    }
  }

  val measureForAllInputDichotomies: Int => Circuit => Map[String, String] = {
    trials =>
     circuit => {
      val nQubits = circuit.register.size
      val measure = measureTimes(trials)
      val inputBasis: List[String] = allDichotomies(nQubits)
      val prepareStates: List[Circuit] = inputBasis.map(dichotomy => dichotomy.toBitRegister.toCircuit)
      inputBasis.zip(prepareStates).map {
        case (dichotomy, pCircuit) =>
          dichotomy -> measure(pCircuit combine circuit)
      }.toMap
    }
  }

}
