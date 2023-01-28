package qroutines.instances.oracles

import cats.data.Reader
import qroutines.blocks.CircuitParams.NumberQubits
import qroutines.blocks.noracle.NOracle
import qroutines.blocks.noracle.OracleDefinitions.{BitShiftValue, BitValue}
import scotty.quantum.gate.StandardGate.X
import scotty.quantum.{BitRegister, Circuit, One, Zero}
import utils.GateUtils.singlePlaceCNOTs
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.{BitRegisterOps, controlMapBitRegister}

trait NDeutschJoszaOracle extends NOracle

object NDeutschJoszaOracle{

  sealed trait NConstantOracle extends NDeutschJoszaOracle {
    type DefiningType = BitValue

    val circuit: Reader[NumberQubits, Circuit] = Reader {
      case NumberQubits(nOracleQubits) =>
        val nQubits = nOracleQubits + 1
        val emptyCircuit = Circuit.apply(Circuit.generateRegister(nQubits))

        val circuit = definingObject.value match {
          case Zero(_) => emptyCircuit
          case One(_) => emptyCircuit combine Circuit(X(nQubits - 1))
        }
        circuit
    }
  }

  final case object ZeroOracle extends NConstantOracle {
    val definingObject: BitValue = BitValue(Zero())
  }

  final case object OneOracle extends NConstantOracle {
    val definingObject: BitValue = BitValue(One())
  }


  final case class BalancedOracle(definingObject: BitShiftValue) extends NDeutschJoszaOracle {
    type DefiningType = BitShiftValue

    val circuit: Reader[NumberQubits, Circuit] = Reader {
      case NumberQubits(nOracleQubits) =>
        val shift: Option[Circuit] = definingObject.value.map(cMap => cMap.encodeE[Int, BitRegister](nOracleQubits).toCircuit)

        val cNOTs = singlePlaceCNOTs((0 until nOracleQubits).map(_ -> nOracleQubits).toMap)
        val balancedInside: Circuit = Circuit(cNOTs: _*)

        val circuit = shift.fold(balancedInside)(sCircuit => sCircuit combine balancedInside combine sCircuit)
        circuit
    }
  }
}
