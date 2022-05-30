package quantumroutines.elementary.algorithms

import quantumroutines.oracle.OracleOutput.{Balanced, Constant, ConstantOrBalanced}
import quantumroutines.oracle.instances.DeutschJoszaOracle
import scotty.quantum.BitRegister
import scotty.quantum.ExperimentResult.StateStats
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister

object DeutschJosza extends DeutschJoszaLike {
  type CircuitOracle = DeutschJoszaOracle
  type CircuitOutput = ConstantOrBalanced

  def interpret(oracle: DeutschJoszaOracle): StateStats => ConstantOrBalanced = {
    neededQubitsStats =>
      val threshold: Double = 0.95
      val statsMap = neededQubitsStats.stats.map { case (bitRegister, times) => bitRegister -> times.toDouble / 1000 }.toMap
      val allZerosRatio: Double = statsMap(("0" * oracle.nOracleQubits).encode[BitRegister])
      if (allZerosRatio > threshold) Constant else Balanced
  }
}