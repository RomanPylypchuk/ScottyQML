package qroutines.instances.oracles

import org.scalatest.flatspec.AnyFlatSpec
import qroutines.blocks.noracle.OracleDefinitions.BitStringValue
import qroutines.instances.oracles.NSimonsOracle.{NIdentityOracle, NRandomOneToOneOracle, TwoToOneOracle}
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.BitRegister
import utils.Measure.measureForAllInputDichotomies
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister

class SimonsOracleTest extends AnyFlatSpec{

  val nQubits: NumberQubits = NumberQubits(3)

  "TwoToOneOracle" should "split the cardinality of domain in half" in {
    val threeQubitTwoToOneOracle = TwoToOneOracle(BitStringValue("110".encode[BitRegister]))
    val resultsTwoToOne = measureForAllInputDichotomies(500)(Some(Set(0,1,2)))(Some(Set(3,4,5)))(threeQubitTwoToOneOracle.circuit(nQubits)).map{
      case (inBitStr, st) => inBitStr -> st.stats.head._1.decode[String].reverse
    }
    assert(resultsTwoToOne.keySet.size / 2 == resultsTwoToOne.values.toSet.size)
  }

  "IdentityOracle" should "map input to output" in {
    val idOracle: NIdentityOracle.type = NIdentityOracle
    val resultsIdentity = measureForAllInputDichotomies(500)(Some(Set(0,1,2)))(Some(Set(3,4,5)))(idOracle.circuit(nQubits)).map{
      case (inBitStr, st) => inBitStr -> st.stats.head._1.decode[String].reverse
    }
    assert(resultsIdentity.forall{case (in, out) => in == out})
  }

  "RandomOneToOne" should "shuffle the domain" in {
    val randomOneToOneOracle = NRandomOneToOneOracle
    val resultsRandomOneToOne = measureForAllInputDichotomies(500)(Some(Set(0,1,2)))(Some(Set(3,4,5)))(randomOneToOneOracle.circuit(nQubits)).map{
      case (inBitStr, st) => inBitStr -> st.stats.head._1.decode[String].reverse
    }
    assert(resultsRandomOneToOne.keySet == resultsRandomOneToOne.values.toSet)
  }

}
