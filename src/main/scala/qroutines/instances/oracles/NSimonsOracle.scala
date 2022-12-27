package qroutines.instances.oracles

import cats.data.Reader
import qroutines.blocks.noracle.NOracle
import qroutines.blocks.noracle.OracleDefinitions.{BitStringValue, BitValue}
import quantumroutines.blocks.CircuitParams.NumberQubits
import quantumroutines.blocks.CircuitWithParams
import scotty.quantum.gate.StandardGate.CNOT
import scotty.quantum.{Bit, Circuit, One, Zero}
import utils.GateUtils.{placeCNOTs, singlePlaceCNOTs}
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.controlMapBitRegister

trait NSimonsOracle extends NOracle

object NSimonsOracle {

  val copyToSecondRegister: Int => Circuit = {
    nOracleQubits =>
      val copyCNOTs: List[CNOT] = singlePlaceCNOTs((0 until nOracleQubits).map(ci => ci -> (ci + nOracleQubits)).toMap)
      Circuit(copyCNOTs: _*)
  }

  sealed trait NOneToOneOracle extends NSimonsOracle {
    type DefiningType = BitValue //One for identity oracle, Zero for random oracle

    val circuit: Reader[NumberQubits, Circuit] = Reader {
      case NumberQubits(nOracleQubits) =>
        val circuit = definingObject.value match {
          case One(_) => copyToSecondRegister(nOracleQubits)
          case Zero(_) =>
            val inIndices: List[Int] = (0 until nOracleQubits).toList
            val shuffledIndices: List[Int] = util.Random.shuffle(inIndices.map(_ + nOracleQubits))
            val shuffleMap: Map[Int, Int] = inIndices.zip(shuffledIndices).toMap
            Circuit(singlePlaceCNOTs(shuffleMap): _*)
        }
        circuit
    }
  }

  case object NIdentityOracle extends NOneToOneOracle {
    val definingObject: BitValue = BitValue(One())
  }

  case object NRandomOneToOneOracle extends NOneToOneOracle {
    val definingObject: BitValue = BitValue(Zero())
  }

  //TODO - Perhaps need a smart constructor, because one could pass all-zeros bitString, which should not be the case here :|
  case class TwoToOneOracle(definingObject: BitStringValue) extends NSimonsOracle {
    type DefiningType = BitStringValue

    val circuit: Reader[NumberQubits, Circuit] = Reader {
      case NumberQubits(nOracleQubits) =>
        val oneIdcs: List[Int] = definingObject.value.decodeE[Int, Map[Int, Bit]](nOracleQubits).collect { case (i, One(_)) => i }.toList
        val nonZeroBitIdx: Int = definingObject.value.values.indexWhere(_ == One())
        val maybeAddMod2: List[CNOT] = placeCNOTs(Map(nonZeroBitIdx -> oneIdcs.map(_ + nOracleQubits)))
        val twoToOneLogic: Circuit = Circuit(maybeAddMod2: _*)
        val circuit = copyToSecondRegister(nOracleQubits) combine twoToOneLogic

        circuit
    }
  }
}
