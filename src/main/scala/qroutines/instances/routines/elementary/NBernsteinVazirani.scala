package qroutines.instances.routines.elementary

import cats.data.Reader
import qroutines.blocks.CircuitParams.NumberQubits
import qroutines.blocks.routine.QuantumRoutine
import qroutines.instances.circuits.elementary.NBernsteinVaziraniCircuit
import qroutines.instances.interpreters.elementary.NBernsteinVaziraniInterpreter
import qroutines.instances.oracles.NInnerProductOracle

case class NBernsteinVazirani(oracle: NInnerProductOracle) extends QuantumRoutine{

  type InParamsType = NumberQubits
  type RoutineCircuitType = NBernsteinVaziraniCircuit

  val qrCircuit: NBernsteinVaziraniCircuit = NBernsteinVaziraniCircuit(oracle)
  val qrMeasureQubits: Reader[NumberQubits,Set[Int]] = Reader{case NumberQubits(nOracleQubits) => (0 until nOracleQubits).toSet}
  val qrInterpreter: NBernsteinVaziraniInterpreter.type = NBernsteinVaziraniInterpreter
}
