package qroutines.instances.interpreters.elementary

import cats.data.{Reader, ValidatedNec}
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import qroutines.blocks.CircuitParams.NumberQubits
import qroutines.blocks.measurements.QuantumMeasurementResult
import qroutines.blocks.routine.QuantumRoutineInterpreter
import qroutines.blocks.routine.QuantumRoutineOutput.BitStringOutput

object NBernsteinVaziraniInterpreter extends QuantumRoutineInterpreter{
  type InParamsType = NumberQubits
  type RoutineOutput = BitStringOutput

  val interpret: Reader[NumberQubits, QuantumMeasurementResult => ValidatedNec[String, BitStringOutput]] =
    Reader{ _ => {
      case QuantumMeasurementResult(neededQubitsStats, _) =>
      val bitString = neededQubitsStats.stats.maxBy { case (_, times) => times }._1
      BitStringOutput(bitString).validNec
     }
    }
}
