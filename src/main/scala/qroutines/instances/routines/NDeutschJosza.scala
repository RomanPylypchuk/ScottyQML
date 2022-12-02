package qroutines.instances.routines

import qroutines.blocks.routine.{QuantumDependentRoutine, QuantumRoutine, QuantumRoutineCircuit}
import qroutines.instances.circuits.elementary.NDeutschJoszaCircuit
import quantumroutines.blocks.CircuitParams.NumberQubits
import qroutines.instances.interpreters.elementary.NDeutschJoszaInterpreter
import qroutines.instances.oracles.NDeutschJoszaOracle

case class NDeutschJosza(usedRoutine: NDeutschJoszaOracle) extends QuantumDependentRoutine{
  type InParamsType = NumberQubits
  type RoutineCircuitType = NDeutschJoszaCircuit

  val qrCircuit: NDeutschJoszaCircuit = NDeutschJoszaCircuit(usedRoutine)
  val qrInterpreter: NDeutschJoszaInterpreter.type = NDeutschJoszaInterpreter
}
