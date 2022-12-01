package qroutines.instances.interpreters.elementary

import breeze.linalg.DenseMatrix
import cats.data.{Reader, ValidatedNec}
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import qroutines.blocks.measurements.MeasurementResult
import qroutines.blocks.routine.QuantumRoutineInterpreter
import qroutines.blocks.routine.QuantumRoutineOutput.BitStringOutput
import quantumroutines.blocks.CircuitParams.NumberQubits
import quantumroutines.oracle.OracleOutput.VectorOutput
import scotty.quantum.BitRegister
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister

object NSimonsInterpreter extends QuantumRoutineInterpreter{
  type InParamsType = NumberQubits
  type RoutineOutput = BitStringOutput

  val interpret: Reader[NumberQubits, MeasurementResult => ValidatedNec[String, BitStringOutput]] =
    Reader{ case NumberQubits(nQubits) => {
      case MeasurementResult(neededQubitStats, _) =>
        val nonZeroDichotomies = neededQubitStats.stats.filter { case (_, occs) => occs != 0 }
        //assert(nonZeroDichotomies.length >= function.nOracleQubits)
        val mostProbable = nonZeroDichotomies.sortBy(_._2).map(_._1).takeRight(nQubits / 2)
        //val outcomesMatrix: DenseMatrix[Int] = DenseMatrix(mostProbable.map(br => br.decode[List[Int]]): _*)

        //TODO - actually need to solve the system of linear equations in binary variables
        val b: BitRegister = "00".encode[BitRegister]
        BitStringOutput(b).validNec
     }
    }
}
