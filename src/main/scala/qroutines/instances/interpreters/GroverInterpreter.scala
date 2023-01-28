package qroutines.instances.interpreters

import cats.data.{Reader, ValidatedNec}
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import qroutines.blocks.measurements.QuantumMeasurementResult
import qroutines.blocks.routine.QuantumRoutineInterpreter
import qroutines.blocks.routine.QuantumRoutineOutput.BitStringsOutput
import quantumroutines.blocks.CircuitParams.NumberQubits

object GroverInterpreter extends QuantumRoutineInterpreter{
  type InParamsType = NumberQubits
  type RoutineOutput = BitStringsOutput

  val interpret: Reader[NumberQubits, QuantumMeasurementResult => ValidatedNec[String, BitStringsOutput]] =
    Reader{ _ => {
      case QuantumMeasurementResult(neededQubitsStats, _) =>
        val bitStrings = neededQubitsStats.stats.collect{case (dichotomy, times) if times != 0 => dichotomy}
        BitStringsOutput(bitStrings).validNec
     }
    }
}
