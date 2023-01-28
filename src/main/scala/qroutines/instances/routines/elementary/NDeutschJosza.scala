package qroutines.instances.routines.elementary

import cats.data.Reader
import qroutines.blocks.CircuitParams.NumberQubits
import qroutines.blocks.routine.QuantumRoutine
import qroutines.instances.circuits.elementary.NDeutschJoszaCircuit
import qroutines.instances.interpreters.elementary.NDeutschJoszaInterpreter
import qroutines.instances.oracles.NDeutschJoszaOracle

case class NDeutschJosza(oracle: NDeutschJoszaOracle) extends QuantumRoutine{
  type InParamsType = NumberQubits
  type RoutineCircuitType = NDeutschJoszaCircuit

  val qrCircuit: NDeutschJoszaCircuit = NDeutschJoszaCircuit(oracle)
  val qrMeasureQubits: Reader[NumberQubits,Set[Int]] = Reader{case NumberQubits(nOracleQubits) => (0 until nOracleQubits).toSet}
  val qrInterpreter: NDeutschJoszaInterpreter.type = NDeutschJoszaInterpreter
}
