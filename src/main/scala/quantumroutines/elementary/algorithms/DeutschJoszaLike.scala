package quantumroutines.elementary.algorithms

import quantumroutines.elementary.ElementaryCircuit
import scotty.quantum.Circuit
import scotty.quantum.gate.StandardGate.{H, X}
import utils.HTensor

trait DeutschJoszaLike extends ElementaryCircuit {
  def preOracle: Int => Circuit =
    nOracleQubits => {
      val initMinusX: Circuit = Circuit.apply(Circuit.generateRegister(nOracleQubits + 1), X(nOracleQubits), H(nOracleQubits))
      initMinusX combine Circuit(HTensor(nOracleQubits): _*)
    }

  def postOracle: Int => Circuit =
    nOracleQubits => Circuit(HTensor(nOracleQubits): _*)
}
