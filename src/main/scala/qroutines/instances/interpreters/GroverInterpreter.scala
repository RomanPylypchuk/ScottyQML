package qroutines.instances.interpreters

import cats.data.{Reader, ValidatedNec}
import qroutines.blocks.measurements.QuantumMeasurementResult
import qroutines.blocks.routine.QuantumRoutineInterpreter
import qroutines.blocks.routine.QuantumRoutineOutput.BitStringOutput
import qroutines.instances.interpreters.elementary.NBernsteinVaziraniInterpreter
import quantumroutines.blocks.CircuitParams.NumberQubits

object GroverInterpreter extends QuantumRoutineInterpreter{
  type InParamsType = NumberQubits
  type RoutineOutput = BitStringOutput

  val interpret: Reader[NumberQubits, QuantumMeasurementResult => ValidatedNec[String, BitStringOutput]] =
    NBernsteinVaziraniInterpreter.interpret
}
