package qroutines.instances.routines.elementary

import cats.data.Reader
import qroutines.blocks.CircuitParams.NumberQubits
import qroutines.blocks.routine.QuantumRoutine
import qroutines.instances.circuits.elementary.DeutschJoszaCircuit
import qroutines.instances.interpreters.elementary.DeutschJoszaInterpreter
import qroutines.instances.oracles.DeutschJoszaOracle

case class DeutschJosza(oracle: DeutschJoszaOracle) extends QuantumRoutine{
  type InParamsType = NumberQubits
  type RoutineCircuitType = DeutschJoszaCircuit

  val qrCircuit: DeutschJoszaCircuit = DeutschJoszaCircuit(oracle)
  val qrMeasureQubits: Reader[NumberQubits,Set[Int]] = Reader{case NumberQubits(nOracleQubits) => (0 until nOracleQubits).toSet}
  val qrInterpreter: DeutschJoszaInterpreter.type = DeutschJoszaInterpreter
}
