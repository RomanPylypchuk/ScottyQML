package qroutines.noracle.instances

import cats.data.Reader
import qroutines.noracle.NOracle
import qroutines.noracle.OracleDefinitions.{BitShiftValue, BitValue}
import quantumroutines.blocks.CircuitParams.NumberQubits
import quantumroutines.blocks.CircuitWithParams
import scotty.quantum.gate.StandardGate.X
import scotty.quantum.{BitRegister, Circuit, One, Zero}
import utils.GateUtils.singlePlaceCNOTs
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.{BitRegisterTo, controlMapBitRegister}

trait NDeutschJoszaOracle extends NOracle

object NDeutschJoszaOracle {

  sealed trait NConstantOracle extends NDeutschJoszaOracle {
    type DefiningType = BitValue

    val circuit: Reader[NumberQubits, CircuitWithParams[NumberQubits]] = Reader {
      case nq@NumberQubits(nOracleQubits) =>
        val nQubits = nOracleQubits + 1
        val emptyCircuit = Circuit.apply(Circuit.generateRegister(nQubits))

        val circuit = definingObject.value match {
          case Zero(_) => emptyCircuit
          case One(_) => emptyCircuit combine Circuit(X(nQubits - 1))
        }
        CircuitWithParams(circuit, nq)
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

    val circuit: Reader[NumberQubits, CircuitWithParams[NumberQubits]] = Reader {
      case nq@NumberQubits(nOracleQubits) =>
        val shift: Option[Circuit] = definingObject.value.map(cMap => cMap.encodeE[Int, BitRegister](nOracleQubits).toCircuit)

        val cNOTs = singlePlaceCNOTs((0 until nOracleQubits).map(_ -> nOracleQubits).toMap)
        val balancedInside: Circuit = Circuit(cNOTs: _*)

        val circuit = shift.fold(balancedInside)(sCircuit => sCircuit combine balancedInside combine sCircuit)
        CircuitWithParams(circuit, nq)
    }
  }
}
