package qroutines.instances.interpreters


import cats.data.{Reader, ValidatedNec}
import cats.implicits.{catsSyntaxTuple2Semigroupal, catsSyntaxValidatedIdBinCompat0}
import qroutines.blocks.measurements.QuantumMeasurementResult
import qroutines.blocks.routine.QuantumRoutineInterpreter
import qroutines.blocks.routine.QuantumRoutineOutput.LongOutput
import quantumroutines.blocks.CircuitParams.OrderFindingParams
import quantumroutines.qft.ModularUnitaryParams
import scotty.quantum.BitRegister
import spire.math.Rational
import utils.Measure.StateStatsOps
import utils.algebra.NumberTheoryRoutines.{lcm, rationalToContinuedFractions}
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.decimalBitRegister

import scala.util.Random


object OrderFindingInterpreter extends QuantumRoutineInterpreter{
    type InParamsType = OrderFindingParams
    type RoutineOutput = LongOutput

  //Convert only first nPhaseQubits to binary fraction representation of \phi
  val statsConvergent: BitRegister => ValidatedNec[String, Rational] = {
    phaseBinary =>

      val nPhaseQubits: Int = phaseBinary.values.length
      val phaseDecimal: Int = phaseBinary.decodeE[Int, Int](nPhaseQubits)
      val estimate: Rational = Rational(phaseDecimal, math.pow(2, nPhaseQubits).toInt)

      val continuedFractions: List[Long] = estimate.decodeE[Option[Int], List[Long]](None)

      val convergentRational: Rational = if (continuedFractions.length <= 3) estimate else {
        val takeConvergent: Int = Random.between(1, continuedFractions.length)
        continuedFractions.encodeE[Option[Int], Rational](Some(takeConvergent))
      }

      if ((estimate - convergentRational).toDouble <= Rational(1, math.pow(2, (nPhaseQubits - 1) / 2).toInt).toDouble)
        convergentRational.validNec
      else ("Randomly chosen convergent " + convergentRational + " for estimate " + estimate + " doesn't satisfy the conditions").invalidNec
  }

  val combineOrders: ModularUnitaryParams =>
    (ValidatedNec[String, Rational], ValidatedNec[String, Rational]) => ValidatedNec[String, Long] = {
    params =>
        (frac1, frac2) =>
          (frac1, frac2).mapN { case (f1, f2) =>
            val (r1, r2) = (f1.denominatorAsLong, f2.denominatorAsLong)
            lcm(r1, r2)
          }.andThen { r =>
            if (math.pow(params.x, r).toInt % params.N == 1) r.validNec else ("Order estimate r=" + r + " is incorrect").invalidNec
          }}

  val interpret: Reader[OrderFindingParams, QuantumMeasurementResult => ValidatedNec[String, LongOutput]] = 
    Reader{ofParams =>
        {case QuantumMeasurementResult(stats, _) =>
          val neededStats = stats.transform(s => s.sortBy{case(_, times) => -times}.take(2)).stats    
            val List((phase1, _), (phase2, _)) = neededStats

            combineOrders(ofParams.modParams)(statsConvergent(phase1), statsConvergent(phase2)) map LongOutput
        }
        
  }    
}
