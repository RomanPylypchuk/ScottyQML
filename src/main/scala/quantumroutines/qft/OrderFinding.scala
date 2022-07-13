package quantumroutines.qft

import breeze.numerics.log2
import cats.data.ValidatedNec
import cats.implicits.{catsSyntaxTuple2Semigroupal, catsSyntaxValidatedIdBinCompat0}
import quantumroutines.blocks.CircuitParams.QPEQubits
import quantumroutines.blocks.CircuitWithParams
import quantumroutines.qft.PhaseEstimation.phaseEstimate
import scotty.quantum.gate.StandardGate.X
import scotty.quantum.{BitRegister, Circuit}
import spire.math.Rational
import utils.Measure.{StateStatsOps, measureTimes}
import utils.algebra.NumberTheoryRoutines.{lcm, rationalToContinuedFractions}
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.decimalBitRegister

import scala.util.Random

object OrderFinding {

  val orderQubits: ModularUnitaryParams => QPEQubits =
    params => {
      val nEigenQubits: Int = log2(params.N).ceil.toInt
      val nPhaseQubits: Int = 2 * nEigenQubits + 1
      QPEQubits(nPhaseQubits, nEigenQubits)
    }

  val qpeOrder: ModularUnitaryParams => ModularExponentiation => CircuitWithParams[QPEQubits] = {
    params =>
      modExp =>
        val modUnitary = modExp.controlPower(params)
        val qDims: QPEQubits = orderQubits(params)
        val phaseCircuit: Circuit = phaseEstimate(qDims)(Circuit(X(qDims.nPhaseQubits + qDims.nEigenQubits - 1)))(modUnitary)
        CircuitWithParams(phaseCircuit, qDims)
  }

  //Need to measure only first nPhaseQubits to get binary fraction representation of \phi
  val statsConvergent: BitRegister => ValidatedNec[String, Rational] = {
    phaseBinary =>
      //val (phaseBinary, _) = qpeStats.stats.maxBy { case (_, times) => times }
      val nPhaseQubits: Int = phaseBinary.values.length
      val phaseDecimal: Int = phaseBinary.decodeE[Int, Int](nPhaseQubits)
      val estimate: Rational = Rational(phaseDecimal, math.pow(2, nPhaseQubits).toInt)

      val continuedFractions: List[Long] = estimate.decodeE[Option[Int], List[Long]](None)
      val takeConvergent: Int = Random.between(1, continuedFractions.length) //TODO - fix, this may fail because of cFractions length = 1
      val convergentRational: Rational = continuedFractions.encodeE[Option[Int], Rational](None) //TODO - how to take Some(takeConvergent) properly here?
      if ((estimate - convergentRational).toDouble <= Rational(1, math.pow(2, (nPhaseQubits - 1) / 2).toInt).toDouble)
        convergentRational.validNec
      else ("Randomly chosen convergent " + convergentRational + " for estimate " + estimate + " doesn't satisfy the conditions").invalidNec
  }

  val combineOrders: ModularUnitaryParams =>
    (ValidatedNec[String, Rational], ValidatedNec[String, Rational]) => ValidatedNec[String, Int] = {
    params =>
        (frac1, frac2) =>
          (frac1, frac2).mapN { case (f1, f2) =>
            val (r1, r2) = (f1.denominatorAsLong, f2.denominatorAsLong)
            lcm(r1.toInt, r2.toInt)
          }.andThen { r =>
            if (math.pow(params.x, r).toInt % params.N == 1) r.validNec else ("Order estimate r=" + r + " is incorrect").invalidNec
          }}

      val order: ModularUnitaryParams => ModularExponentiation => ValidatedNec[String, Int] = {
        params =>
          modExp =>
            val orderBlock: CircuitWithParams[QPEQubits] = qpeOrder(params)(modExp)
            val neededStats = measureTimes(10)(orderBlock.circuit) //TODO - factor out 10 times
              .forQubits((0 until orderBlock.params.nPhaseQubits).toSet)
              .transform(s => s.sortBy{case(_, times) => -times}.take(2)).stats

            val List((phase1, _), (phase2, _)) = neededStats

            combineOrders(params)(statsConvergent(phase1), statsConvergent(phase2))
      }

}