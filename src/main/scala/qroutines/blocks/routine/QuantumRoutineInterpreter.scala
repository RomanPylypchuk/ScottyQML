package qroutines.blocks.routine

import cats.data.{Reader, ValidatedNec}
import qroutines.blocks.measurements.QuantumMeasurementResult
import quantumroutines.blocks.CircuitParams
import scotty.quantum.ExperimentResult.StateStats

trait QuantumRoutineInterpreter {
  type InParamsType <: CircuitParams
  type RoutineOutput <: QuantumRoutineOutput

  val interpret: Reader[InParamsType, QuantumMeasurementResult => ValidatedNec[String, RoutineOutput]]
}
