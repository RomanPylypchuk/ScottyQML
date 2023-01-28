package qroutines.blocks.routine

import cats.data.{Reader, ValidatedNec}
import qroutines.blocks.CircuitParams
import qroutines.blocks.measurements.QuantumMeasurementResult

trait QuantumRoutineInterpreter {
  type InParamsType <: CircuitParams
  type RoutineOutput <: QuantumRoutineOutput

  val interpret: Reader[InParamsType, QuantumMeasurementResult => ValidatedNec[String, RoutineOutput]]
}
