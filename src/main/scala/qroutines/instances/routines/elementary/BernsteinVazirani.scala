package qroutines.instances.routines.elementary

import cats.data.Reader
import qroutines.blocks.CircuitParams.NumberQubits
import qroutines.blocks.routine.QuantumRoutine
import qroutines.instances.circuits.elementary.BernsteinVaziraniCircuit
import qroutines.instances.interpreters.elementary.BernsteinVaziraniInterpreter
import qroutines.instances.oracles.InnerProductOracle

case class BernsteinVazirani(oracle: InnerProductOracle) extends QuantumRoutine{

  type InParamsType = NumberQubits
  type RoutineCircuitType = BernsteinVaziraniCircuit

  val qrCircuit: BernsteinVaziraniCircuit = BernsteinVaziraniCircuit(oracle)
  val qrMeasureQubits: Reader[NumberQubits,Set[Int]] = Reader{case NumberQubits(nOracleQubits) => (0 until nOracleQubits).toSet}
  val qrInterpreter: BernsteinVaziraniInterpreter.type = BernsteinVaziraniInterpreter
}
