package qroutines.instances.interpreters

import cats.data.{Reader, ValidatedNec}
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import qroutines.blocks.CircuitParams.{NumberQubits, QPEParams}
import qroutines.blocks.measurements.QuantumMeasurementResult
import qroutines.blocks.routine.QuantumRoutineInterpreter
import qroutines.blocks.routine.QuantumRoutineOutput.RationalOutput
import spire.math.Rational
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.{BitRegisterOps, decimalBitRegister}


object PhaseEstimationInterpreter extends QuantumRoutineInterpreter {
  type InParamsType = QPEParams
  type RoutineOutput = RationalOutput

  val interpret: Reader[QPEParams, QuantumMeasurementResult => ValidatedNec[String, RationalOutput]] = Reader {
    case QPEParams(qpeParams, _, _) => {
      case QuantumMeasurementResult(mStats, _) =>
        val (phaseBinary, _) = mStats.stats.maxBy(_._2)
        val NumberQubits(nPhaseQubits) = qpeParams.nPhaseQubits
        val phaseDecimal: Int = phaseBinary.reverse.decodeE[Int, Int](nPhaseQubits)
        val estimate: Rational = Rational(phaseDecimal, math.pow(2, nPhaseQubits).toInt)
        RationalOutput(estimate).validNec
      //TODO - consider cases of bad estimates(for .invalidNec), e.g. when distribution is rather uniform
    }
  }
}
