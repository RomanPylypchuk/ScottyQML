package quantumroutines.elementary.algorithms

import quantumroutines.oracle.OracleOutput.VectorOutput
import quantumroutines.oracle.instances.InnerProductOracle
import scotty.quantum.ExperimentResult.StateStats

object BernsteinVazirani extends DeutschJoszaLike {
  type CircuitOracle = InnerProductOracle
  type CircuitOutput = VectorOutput

  def interpret(oracle: InnerProductOracle): StateStats => VectorOutput = {
    neededQubitsStats =>
      VectorOutput(neededQubitsStats.stats.maxBy{case (_, times) => times}._1)
  }
}