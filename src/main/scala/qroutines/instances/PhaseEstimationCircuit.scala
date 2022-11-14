package qroutines.instances

import cats.data.Reader
import qroutines.QuantumRoutineCircuit.DependentQuantumRoutineCircuit
import quantumroutines.blocks.CircuitParams.{NumberQubits, QPEParams}
import quantumroutines.blocks.CircuitWithParams

object PhaseEstimationCircuit extends DependentQuantumRoutineCircuit {
  type InParamsType = QPEParams
  type UsedRoutineType = QFTCircuit.type
  type OutParamsType = QPEParams

  val usedRoutine: QFTCircuit.type = QFTCircuit
  val inParamsToUsedRoutineParams: QPEParams => NumberQubits = _ => NumberQubits(1)
  val circuit: Reader[QPEParams, CircuitWithParams[QPEParams]] = ???
}
