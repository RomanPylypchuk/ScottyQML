package qroutines.instances.routines.elementary

import qroutines.blocks.routine.{QuantumRoutine, QuantumRoutineInterpreter}
import qroutines.instances.circuits.elementary.NSimonsCircuit
import qroutines.instances.interpreters.elementary.NSimonsInterpreter
import qroutines.instances.oracles.NSimonsOracle
import quantumroutines.blocks.CircuitParams.NumberQubits

case class NSimons(oracle: NSimonsOracle) extends QuantumRoutine{
  type InParamsType = NumberQubits
  type RoutineCircuitType = NSimonsCircuit

  val qrCircuit: NSimonsCircuit = NSimonsCircuit(oracle)
  val qrInterpreter: NSimonsInterpreter.type = NSimonsInterpreter
}
