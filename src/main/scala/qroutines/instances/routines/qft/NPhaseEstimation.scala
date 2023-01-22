package qroutines.instances.routines.qft

import cats.data.Reader
import qroutines.blocks.routine.QuantumRoutine
import qroutines.instances.circuits.PhaseEstimationCircuit
import qroutines.instances.interpreters.PhaseEstimationInterpreter
import quantumroutines.blocks.CircuitParams.{NumberQubits, QPEParams}

object NPhaseEstimation extends QuantumRoutine{
  type InParamsType = QPEParams

  val qrCircuit: PhaseEstimationCircuit.type = PhaseEstimationCircuit
  val qrMeasureQubits: Reader[NumberQubits, Set[Int]] = Reader(nPhaseQubits => (0 until nPhaseQubits.nQubits).toSet)
  val qrInterpreter: PhaseEstimationInterpreter.type = PhaseEstimationInterpreter
}
