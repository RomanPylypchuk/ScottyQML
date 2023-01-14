package qroutines.instances.oracles

import org.scalatest.flatspec.AnyFlatSpec
import qroutines.blocks.noracle.OracleDefinitions.BitStringValue
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.BitRegister
import spire.algebra.InnerProductSpace
import utils.Measure.{StateStatsOps, measureForAllInputDichotomies, measureTimes}
import utils.algebra.InnerProductMod.InnerProductMod
import utils.factory.BitRegisterFactory.{stringBitRegister, bitBitRegister}
import utils.codec.BiCodec.BiCodecSyntax

class InnerProductOracleTest extends AnyFlatSpec{

  val nQubits: NumberQubits = NumberQubits(3)

  "Inner Product Oracle" should "output inner product (mod 2)" in {
    val iBits = "101".encode[BitRegister]
    val iOracle = NInnerProductOracle(BitStringValue(iBits))
    val allInputStats = measureForAllInputDichotomies(1000)(Some(Set(0,1,2)))(Some(Set(3)))(iOracle.circuit(nQubits))

    allInputStats.forall{case (inD, dStats) =>
      val (outD, _) = dStats.stats.maxBy(_._2)
      val mulCorrectResult = iBits.dot(inD.encode[BitRegister], 2)
      outD.decode[String] == mulCorrectResult.toString
    }
  }
}
