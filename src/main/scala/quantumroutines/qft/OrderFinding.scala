package quantumroutines.qft

import breeze.numerics.log2
import cats.data.ValidatedNec
import cats.implicits.{catsSyntaxTuple2Semigroupal, catsSyntaxValidatedIdBinCompat0}
import quantumroutines.qft.PhaseEstimation.phaseEstimate
import scotty.quantum.ExperimentResult.StateStats
import scotty.quantum.{BitRegister, Circuit}
import spire.math.Rational
import utils.algebra.NumberTheoryRoutines.{lcm, rationalToContinuedFractions}
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.decimalBitRegister

import scala.util.Random

object OrderFinding {

  //Assume we have a reasonably efficient modular exponentiation implementation U_x_N
  val UxN: Int => Int => (Int, Int) => Circuit =
    x =>
      N =>
        (ci, j) => ???

  val qpeOrder: Int => Int => Circuit = {
    x =>
      N =>
        val nEigenQubits: Int = log2(N).ceil.toInt
        val nPhaseQubits: Int = 2 * nEigenQubits + 1
        phaseEstimate(nPhaseQubits)(nEigenQubits)(UxN(x)(N))
  }

  //Need to measure only first nPhaseQubits to get binary fraction representation of \phi
  val statsConvergent: StateStats => ValidatedNec[String, Rational] = {
    qpeStats =>
      val (phaseBinary, _) = qpeStats.stats.maxBy { case (_, times) => times }
      val nPhaseQubits: Int = phaseBinary.values.length
      val phaseDecimal: Int = BitRegister(phaseBinary.values.reverse: _*).decodeE[Int, Int](nPhaseQubits)
      val estimate: Rational = Rational(phaseDecimal, math.pow(2, nPhaseQubits).toInt)

      val continuedFractions: List[Long] = estimate.decodeE[Option[Int], List[Long]](None)
      val takeConvergent: Int = Random.between(1, continuedFractions.length)
      val convergentRational: Rational = continuedFractions.encodeE[Option[Int], Rational](None) //TODO - how to take Some(takeConvergent) properly here?
      if ((estimate - convergentRational).toDouble <= Rational(1, math.pow(2, (nPhaseQubits - 1) / 2).toInt).toDouble)
        convergentRational.validNec
      else ("Randomly chosen convergent " + convergentRational + " for estimate " + estimate + " doesn't satisfy the conditions").invalidNec
  }

  val order: Int => Int =>
    (ValidatedNec[String, Rational], ValidatedNec[String, Rational]) => ValidatedNec[String, Int] = {
    x =>
      N =>
        (frac1, frac2) =>
          (frac1, frac2).mapN { case (f1, f2) =>
            val (r1, r2)= (f1.denominatorAsLong, f2.denominatorAsLong)
            lcm(r1.toInt, r2.toInt)
          }.andThen{r =>
            if (math.pow(x, r).toInt % N == 1) r.validNec else ("Order estimate r=" + r + " is incorrect").invalidNec
          }
  }
}
