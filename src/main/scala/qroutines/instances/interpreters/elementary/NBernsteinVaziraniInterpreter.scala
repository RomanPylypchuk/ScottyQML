package qroutines.instances.interpreters.elementary

import cats.data.{Reader, ValidatedNec}
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import qroutines.blocks.measurements.MeasurementResult
import qroutines.blocks.routine.QuantumRoutineOutput.BitStringOutput
import qroutines.blocks.routine.{QuantumRoutineInterpreter, QuantumRoutineOutput}
import quantumroutines.blocks.CircuitParams.NumberQubits

object NBernsteinVaziraniInterpreter extends QuantumRoutineInterpreter{
  type InParamsType = NumberQubits
  type RoutineOutput = BitStringOutput

  val interpret: Reader[NumberQubits, MeasurementResult => ValidatedNec[String, BitStringOutput]] =
    Reader{ case NumberQubits(_) => {
      case MeasurementResult(neededQubitsStats, _) =>
      val bitString = neededQubitsStats.stats.maxBy { case (_, times) => times }._1
      BitStringOutput(bitString).validNec
     }
    }
}
