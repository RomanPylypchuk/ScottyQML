package qroutines.instances.oracles

import org.scalatest.flatspec.AnyFlatSpec
import qroutines.blocks.noracle.OracleDefinitions.BitShiftValue
import qroutines.instances.oracles.NDeutschJoszaOracle.{BalancedOracle, OneOracle, ZeroOracle}
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.BitRegister
import scotty.quantum.ExperimentResult.StateStats
import utils.Measure.measureForAllInputDichotomies
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister

class DeutschJoszaOracleTest extends AnyFlatSpec {

  val nOracleQubits: NumberQubits = NumberQubits(3)
  val measureOracle: NDeutschJoszaOracle => Map[String, StateStats] =
    oracle => {
      measureForAllInputDichotomies(1000)(Some(Set(0,1,2)))(Some(Set(3)))(oracle.circuit(nOracleQubits))
    }

  "One Oracle output" should "be One" in {
    val allInputsStats = measureOracle(OneOracle)
    assert(
      allInputsStats.forall{ case (_, dStats) =>
      dStats.stats == List(
        ("1".encode[BitRegister], 1000)
      )}
    )
  }

  "Balanced Oracle" should "be Balanced" in {
    val allInputsStats = measureOracle(BalancedOracle(BitShiftValue(None)))
    val allOutputs = allInputsStats.values.map(_.stats.head._1)
    val allOutputsCounts = allOutputs.groupMapReduce(identity)(_ => 1)(_ + _)
    assert(allOutputsCounts("0".encode[BitRegister]) == allOutputsCounts("1".encode[BitRegister]))
  }

}
