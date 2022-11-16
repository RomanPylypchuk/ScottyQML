package quantumroutines.oracle.instances

import quantumroutines.oracle.{HasBitString, Oracle}
import scotty.quantum.gate.StandardGate.CNOT
import scotty.quantum.{Bit, BitRegister, Circuit, One}
import utils.GateUtils.{placeCNOTs, singlePlaceCNOTs}
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.controlMapBitRegister

sealed trait SimonsOracle extends Oracle{
  //type OutputType = VectorOutput
}

object SimonsOracle {
  val copyToSecondRegister: Int => Circuit = {
    nOracleQubits =>
      val copyCNOTs: List[CNOT] = singlePlaceCNOTs((0 until nOracleQubits).map(ci => ci -> (ci + nOracleQubits)).toMap)
      Circuit(copyCNOTs: _*)
  }

  sealed trait OneToOneOracle extends SimonsOracle

  case class IdentityOracle(nOracleQubits: Int) extends OneToOneOracle {
    def oracle: Circuit = copyToSecondRegister(nOracleQubits)
  }

  case class RandomOneToOne(nOracleQubits: Int) extends OneToOneOracle {
    def oracle: Circuit = {
      val inIndices: List[Int] = (0 until nOracleQubits).toList
      val shuffledIndices: List[Int] = util.Random.shuffle(inIndices.map(_ + nOracleQubits))
      val shuffleMap: Map[Int, Int] = inIndices.zip(shuffledIndices).toMap
      Circuit(singlePlaceCNOTs(shuffleMap): _*)
    }
  }

  //TODO - Perhaps need a smart constructor, because one could pass all-zeros bitString, which should not be the case here :|
  case class TwoToOneOracle(nOracleQubits: Int, bitString: BitRegister) extends SimonsOracle with HasBitString {
    def oracle: Circuit = {
      val oneIdcs: List[Int] = bitString.decodeE[Int, Map[Int, Bit]](nOracleQubits).collect { case (i, One(_)) => i }.toList
      val nonZeroBitIdx: Int = bitString.values.indexWhere(_ == One())
      val maybeAddMod2: List[CNOT] = placeCNOTs(Map(nonZeroBitIdx -> oneIdcs.map(_ + nOracleQubits)))
      val twoToOneLogic: Circuit = Circuit(maybeAddMod2: _*)
      copyToSecondRegister(nOracleQubits) combine twoToOneLogic
    }
  }
}