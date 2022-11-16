package quantumroutines.elementary.algorithms

import breeze.linalg.DenseMatrix
import quantumroutines.elementary.ElementaryCircuit
import quantumroutines.oracle.OracleOutput.VectorOutput
import quantumroutines.oracle.instances.SimonsOracle
import scotty.quantum.ExperimentResult.StateStats
import scotty.quantum.{BitRegister, Circuit}
import utils.GateUtils.HTensor
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.{bitBitRegister, stringBitRegister}

object Simons extends ElementaryCircuit {
  type CircuitOracle = SimonsOracle
  type CircuitOutput = VectorOutput

  def preOracle: Int => Circuit =
    nOracleQubits => {
      val init: Circuit = Circuit(Circuit.generateRegister(nOracleQubits * 2))
      init combine Circuit(HTensor(nOracleQubits): _*)
    }

  def postOracle: Int => Circuit = nOracleQubits => Circuit(HTensor(nOracleQubits): _*)

  def interpret(oracle: SimonsOracle): StateStats => VectorOutput = {
    neededQubitStats =>
      val nonZeroDichotomies = neededQubitStats.stats.filter{case (_, occs) => occs != 0}
      //assert(nonZeroDichotomies.length >= function.nOracleQubits)
      val mostProbable = nonZeroDichotomies.sortBy(_._2).map(_._1).takeRight(oracle.nOracleQubits)
      val outcomesMatrix: DenseMatrix[Int] = DenseMatrix(mostProbable.map(br => br.decode[List[Int]]) :_*)
      println(outcomesMatrix)
      //TODO - actually need to solve the system of linear equations in binary variables
      VectorOutput("00".encode[BitRegister])
  }
}