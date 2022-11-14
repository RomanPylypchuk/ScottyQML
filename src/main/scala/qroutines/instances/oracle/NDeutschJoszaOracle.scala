package qroutines.instances.oracle

import cats.data.Reader
import qroutines.NOracle
import quantumroutines.blocks.CircuitParams.NumberQubits
import quantumroutines.blocks.CircuitWithParams
import scotty.quantum._
import scotty.quantum.gate.StandardGate.X
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.{BitRegisterTo, controlMapBitRegister}
import utils.singlePlaceCNOTs

sealed trait NDeutschJoszaOracle extends NOracle

object NDeutschJoszaOracle {

  sealed trait NConstantOracle extends NDeutschJoszaOracle {

    def output: Bit

    val circuit: Reader[NumberQubits, CircuitWithParams[NumberQubits]] = {
      Reader {
        case nq @ NumberQubits(nOracleQubits) =>

          val nQubits = nOracleQubits + 1
          val emptyCircuit = Circuit.apply(Circuit.generateRegister(nQubits))
          val circuit = output match {
            case Zero(_) => emptyCircuit
            case One(_) => emptyCircuit combine Circuit(X(nQubits - 1))
          }
          CircuitWithParams(circuit, nq)
      }
    }
  }

  final case class ZeroOracle(nOracleQubits: Int) extends NConstantOracle {
    def output: Bit = Zero()
  }

  final case class OneOracle(nOracleQubits: Int) extends NConstantOracle {
    def output: Bit = One()
  }

  final case class NBalancedOracle(nOracleQubits: Int, balanceShift: Option[Map[Int, Bit]] = None) extends NDeutschJoszaOracle {

    val circuit: Reader[NumberQubits, CircuitWithParams[NumberQubits]] =
     Reader{
       case nq @ NumberQubits(nOracleQubits) =>
         val shift: Option[Circuit] = balanceShift.map(cMap => cMap.encodeE[Int, BitRegister](nOracleQubits).toCircuit)

         val cNOTs = singlePlaceCNOTs((0 until nOracleQubits).map(_ -> nOracleQubits).toMap)
         val balancedInside: Circuit = Circuit(cNOTs: _*)

         val circuit = shift.fold(balancedInside)(sCircuit => sCircuit combine balancedInside combine sCircuit)
         CircuitWithParams(circuit, nq)
     }
  }
}
