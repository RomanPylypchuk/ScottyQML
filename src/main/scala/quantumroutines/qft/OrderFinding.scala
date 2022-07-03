package quantumroutines.qft

import breeze.numerics.log2
import quantumroutines.qft.PhaseEstimation.phaseEstimate
import scotty.quantum.Circuit

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
}
