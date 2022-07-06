package quantumroutines.qft

import breeze.numerics.log2
import cats.data.ValidatedNec
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import quantumroutines.qft.PhaseEstimation.phaseEstimate
import scotty.quantum.ExperimentResult.StateStats
import scotty.quantum.{BitRegister, Circuit}
import spire.math.Rational
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.decimalBitRegister

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
      val (phaseBinary, _) = qpeStats.stats.maxBy{case (_, times) => times}
      val nPhaseQubits: Int = phaseBinary.values.length
      val phaseDecimal: Int = BitRegister(phaseBinary.values.reverse :_*).decodeE[Int, Int](nPhaseQubits)
      val estimate: Rational = Rational(phaseDecimal, math.pow(2, nPhaseQubits).toInt)

      "yo".invalidNec
  }
}
