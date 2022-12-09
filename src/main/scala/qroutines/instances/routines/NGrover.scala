package qroutines.instances.routines

import cats.data.Reader
import qroutines.blocks.routine.QuantumRoutine
import qroutines.instances.circuits.GroverCircuit
import qroutines.instances.interpreters.GroverInterpreter
import qroutines.instances.oracles.GroverOracle
import quantumroutines.blocks.CircuitParams.NumberQubits

case class NGrover(oracle: GroverOracle) extends QuantumRoutine{
  type InParamsType = NumberQubits
  type RoutineCircuitType = GroverCircuit

  val qrCircuit: GroverCircuit = GroverCircuit(oracle)
  val qrMeasureQubits: Reader[NumberQubits, Set[Int]] = Reader{case NumberQubits(nOracleQubits) => (0 until nOracleQubits).toSet}
  val qrInterpreter: GroverInterpreter.type = GroverInterpreter
}
