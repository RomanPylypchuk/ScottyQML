package qroutines.instances.routines.elementary

import qroutines.blocks.routine.QuantumRoutine
import qroutines.instances.circuits.elementary.NBernsteinVaziraniCircuit
import qroutines.instances.interpreters.elementary.NBernsteinVaziraniInterpreter
import qroutines.instances.oracles.NInnerProductOracle
import quantumroutines.blocks.CircuitParams.NumberQubits

case class NBernsteinVazirani(oracle: NInnerProductOracle) extends QuantumRoutine{

  type InParamsType = NumberQubits
  type RoutineCircuitType = NBernsteinVaziraniCircuit

  val qrCircuit: NBernsteinVaziraniCircuit = NBernsteinVaziraniCircuit(oracle)
  val qrInterpreter: NBernsteinVaziraniInterpreter.type = NBernsteinVaziraniInterpreter
}
