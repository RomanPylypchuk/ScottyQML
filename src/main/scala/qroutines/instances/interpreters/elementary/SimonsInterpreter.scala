package qroutines.instances.interpreters.elementary

import breeze.linalg.DenseMatrix
import cats.data.{Reader, ValidatedNec}
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import qroutines.blocks.CircuitParams.NumberQubits
import qroutines.blocks.measurements.QuantumMeasurementResult
import qroutines.blocks.routine.QuantumRoutineInterpreter
import qroutines.blocks.routine.QuantumRoutineOutput.{OneOrTwoToOne, OneToOne, TwoToOne}
import scotty.quantum.BitRegister
import utils.BinarySolver.binarySolverBruteForce
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.bitBitRegister

object SimonsInterpreter extends QuantumRoutineInterpreter {
  type InParamsType = NumberQubits
  type RoutineOutput = OneOrTwoToOne

  val interpret: Reader[NumberQubits, QuantumMeasurementResult => ValidatedNec[String, OneOrTwoToOne]] =
    Reader { case NumberQubits(nQubits) => {
      case QuantumMeasurementResult(neededQubitStats, _) =>
        val nonZeroDichotomies = neededQubitStats.stats.filter { case (_, occs) => occs != 0 }
        val mostProbable = nonZeroDichotomies.sortBy(_._2).map(_._1).takeRight(nQubits / 2)
        val outcomesMatrix: DenseMatrix[Int] = DenseMatrix(mostProbable.map(br => br.decode[List[Int]]): _*)
        val maybeOracleBits: Option[BitRegister] = binarySolverBruteForce(outcomesMatrix) //TODO - replace with decent solver

        val result: ValidatedNec[String, OneOrTwoToOne] = maybeOracleBits.fold((OneToOne : OneOrTwoToOne).validNec[String]){
            br => TwoToOne(br).validNec[String]}
        result
     }
    }
}
