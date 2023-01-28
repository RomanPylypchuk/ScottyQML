package qroutines.instances.routines.elementary

import cats.data.Reader
import qroutines.blocks.CircuitParams.NumberQubits
import qroutines.blocks.routine.QuantumRoutine
import qroutines.instances.circuits.elementary.SimonsCircuit
import qroutines.instances.interpreters.elementary.SimonsInterpreter
import qroutines.instances.oracles.SimonsOracle

case class Simons(oracle: SimonsOracle) extends QuantumRoutine{
  type InParamsType = NumberQubits
  type RoutineCircuitType = SimonsCircuit

  val qrCircuit: SimonsCircuit = SimonsCircuit(oracle)
  val qrMeasureQubits: Reader[NumberQubits, Set[Int]] =
    Reader{case NumberQubits(nOracleQubits) =>
      (0 until nOracleQubits).toSet}
  val qrInterpreter: SimonsInterpreter.type = SimonsInterpreter
}
