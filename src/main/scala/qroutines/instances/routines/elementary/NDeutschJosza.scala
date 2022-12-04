package qroutines.instances.routines.elementary

import qroutines.blocks.routine.QuantumRoutine
import qroutines.instances.circuits.elementary.NDeutschJoszaCircuit
import qroutines.instances.interpreters.elementary.NDeutschJoszaInterpreter
import qroutines.instances.oracles.NDeutschJoszaOracle
import quantumroutines.blocks.CircuitParams.NumberQubits

case class NDeutschJosza(oracle: NDeutschJoszaOracle) extends QuantumRoutine{
  type InParamsType = NumberQubits
  type RoutineCircuitType = NDeutschJoszaCircuit

  val qrCircuit: NDeutschJoszaCircuit = NDeutschJoszaCircuit(oracle)
  val qrInterpreter: NDeutschJoszaInterpreter.type = NDeutschJoszaInterpreter
}
