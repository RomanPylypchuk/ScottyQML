package quantumroutines.simons

import quantumroutines.simons.SimonsOracle.{IdentityOracle, RandomOneToOne, TwoToOneOracle}
import scotty.quantum.{BitRegister, Circuit}
import utils.BitRegisterFactory.stringBitRegister
import utils.Measure.measureForAllInputDichotomies
import utils.codec.BiCodec.BiCodecSyntax

object SimonsOracleTest extends App{

  //TwoToOneOracle Test
  val threeQubitTwoToOneOracle: Circuit = TwoToOneOracle(3, "110".encode[BitRegister]).oracle
  val resultsTwoToOne = measureForAllInputDichotomies(500)(Some(Set(0,1,2)))(Some(Set(3,4,5)))(threeQubitTwoToOneOracle).map{
    case (inBitStr, st) => inBitStr -> st.stats.head._1.decode[String].reverse
  }
  assert(resultsTwoToOne.keySet.size / 2 == resultsTwoToOne.values.toSet.size)


  //OneToOneOracle Test
  //IdentityOracle Test
  val idOracle: Circuit = IdentityOracle(3).oracle
  val identityResults = measureForAllInputDichotomies(500)(Some(Set(0,1,2)))(Some(Set(3,4,5)))(idOracle).map{
    case (inBitStr, st) => inBitStr -> st.stats.head._1.decode[String].reverse
  }
  assert(identityResults.forall{case (in, out) => in == out})

  //RandomOracle Test
  val randomOneToOneOracle = RandomOneToOne(3)
  val resultsRandomOneToOne = measureForAllInputDichotomies(500)(Some(Set(0,1,2)))(Some(Set(3,4,5)))(randomOneToOneOracle.oracle).map{
    case (inBitStr, st) => inBitStr -> st.stats.head._1.decode[String].reverse
  }
  assert(resultsRandomOneToOne.keySet == resultsRandomOneToOne.values.toSet)

}
