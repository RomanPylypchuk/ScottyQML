package qroutines.instances.routines.elementary

import cats.data.Reader
import qroutines.blocks.CircuitParams.NumberQubits
import qroutines.blocks.routine.QuantumRoutine
import qroutines.instances.circuits.elementary.NSimonsCircuit
import qroutines.instances.interpreters.elementary.NSimonsInterpreter
import qroutines.instances.oracles.NSimonsOracle

case class NSimons(oracle: NSimonsOracle) extends QuantumRoutine{
  type InParamsType = NumberQubits
  type RoutineCircuitType = NSimonsCircuit

  val qrCircuit: NSimonsCircuit = NSimonsCircuit(oracle)
  val qrMeasureQubits: Reader[NumberQubits, Set[Int]] =
    Reader{case NumberQubits(nOracleQubits) =>
      (0 until nOracleQubits).toSet}
  val qrInterpreter: NSimonsInterpreter.type = NSimonsInterpreter
}
