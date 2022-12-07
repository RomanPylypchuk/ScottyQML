package qroutines.instances.interpreters

import cats.data.{Reader, ValidatedNec}
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import qroutines.blocks.routine.QuantumRoutineInterpreter
import quantumroutines.blocks.CircuitParams.OrderFindingParams
import qroutines.blocks.routine.QuantumRoutineOutput.LongOutput
import qroutines.blocks.measurements.QuantumMeasurementResult
import quantumroutines.qft.ModularUnitaryParams
import utils.algebra.NumberTheoryRoutines.gcd


object ShorInterpreter extends QuantumRoutineInterpreter{
    type InParamsType = OrderFindingParams
    type RoutineOutput = LongOutput

      val factorFromOrder: Reader[OrderFindingParams, LongOutput => ValidatedNec[String, LongOutput]] =
    Reader {
      case OrderFindingParams(params, _) =>
        { case LongOutput(r) =>
            if (r % 2 == 0 && math.abs(math.pow(params.x, r / 2) % params.N) != 1) {
              val maybeFactors = List(1, -1).map(shift => gcd((math.pow(params.x, r / 2).toLong + shift) % params.N, params.N)._1)
              maybeFactors
                .find {
                  params.N % _ == 0
                }
                .fold[ValidatedNec[String, LongOutput]](s"None of found factors $maybeFactors are correct".invalidNec)(LongOutput(_).validNec)
            }
            else s"Found improper order $r".invalidNec
          }
    }

    val interpret: Reader[OrderFindingParams, QuantumMeasurementResult => ValidatedNec[String, LongOutput]] = 
        for{
            foundOrder <- OrderFindingInterpreter.interpret       
            findFactor <- factorFromOrder
        } yield foundOrder.andThen(_.andThen(findFactor))
}
