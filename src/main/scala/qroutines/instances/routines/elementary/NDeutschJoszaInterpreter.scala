package qroutines.instances.routines.elementary

import cats.data.{Reader, ValidatedNec}
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import qroutines.blocks.routine.QuantumRoutineOutput.ConstantOrBalanced
import qroutines.blocks.routine.QuantumRoutineOutput.{Balanced, Constant}
import qroutines.blocks.routine.QuantumRoutineInterpreter
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.BitRegister
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister
import qroutines.blocks.measurements.MeasurementResult


object NDeutschJoszaInterpreter extends QuantumRoutineInterpreter{
  type InParamsType = NumberQubits
  type RoutineOutput = ConstantOrBalanced

  val interpret: Reader[NumberQubits, MeasurementResult => ValidatedNec[String, ConstantOrBalanced]] =
    Reader{
      case NumberQubits(nQubits) =>
        {case MeasurementResult(neededQubitsStats, mParams) =>
          val threshold: Double = 0.95
          val statsMap = neededQubitsStats.stats.map { case (bitRegister, times) => bitRegister -> times.toDouble / mParams.times }.toMap
          val allZerosRatio: Double = statsMap(("0" * (nQubits - 1)).encode[BitRegister])
          if (allZerosRatio > threshold) Constant.validNec else Balanced.validNec
         }
    }
}
