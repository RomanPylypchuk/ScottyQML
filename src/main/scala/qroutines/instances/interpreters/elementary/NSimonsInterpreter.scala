package qroutines.instances.interpreters.elementary

import breeze.linalg.DenseMatrix
import cats.data.{Reader, ValidatedNec}
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import qroutines.blocks.measurements.MeasurementResult
import qroutines.blocks.routine.QuantumRoutineInterpreter
import qroutines.blocks.routine.QuantumRoutineOutput.{BitStringOutput, OneOrTwoToOne, OneToOne, TwoToOne}
import quantumroutines.blocks.CircuitParams.NumberQubits
import quantumroutines.oracle.OracleOutput.VectorOutput
import scotty.quantum.BitRegister
import utils.BinarySolver.binarySolverBruteForce
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.{bitBitRegister, stringBitRegister}

object NSimonsInterpreter extends QuantumRoutineInterpreter {
  type InParamsType = NumberQubits
  type RoutineOutput = OneOrTwoToOne

  val interpret: Reader[NumberQubits, MeasurementResult => ValidatedNec[String, OneOrTwoToOne]] =
    Reader { case NumberQubits(nQubits) => {
      case MeasurementResult(neededQubitStats, _) =>
        val nonZeroDichotomies = neededQubitStats.stats.filter { case (_, occs) => occs != 0 }
        val mostProbable = nonZeroDichotomies.sortBy(_._2).map(_._1).takeRight(nQubits / 2)
        val outcomesMatrix: DenseMatrix[Int] = DenseMatrix(mostProbable.map(br => br.decode[List[Int]]): _*)
        val maybeOracleBits: Option[BitRegister] = binarySolverBruteForce(outcomesMatrix) //TODO - replace with decent solver

          val result: ValidatedNec[String, OneOrTwoToOne] = maybeOracleBits.fold("Could not solve binary system".invalidNec[OneOrTwoToOne]){
            case br if br.decode[List[Int]].forall(_ == 0) =>  OneToOne.validNec[String]
            case br => TwoToOne(br).validNec[String]
          }
        result
     }
    }
}
