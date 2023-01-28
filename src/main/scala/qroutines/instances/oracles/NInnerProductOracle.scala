package qroutines.instances.oracles

import cats.data.Reader
import qroutines.blocks.CircuitParams.NumberQubits
import qroutines.blocks.noracle.NOracle
import qroutines.blocks.noracle.OracleDefinitions.BitStringValue
import scotty.quantum.{Bit, Circuit, One}
import utils.GateUtils.singlePlaceCNOTs
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.controlMapBitRegister

case class NInnerProductOracle(definingObject: BitStringValue) extends NOracle {
  type DefiningType = BitStringValue

  val circuit: Reader[NumberQubits, Circuit] = Reader {
    case NumberQubits(nOracleQubits) =>
      val cNOTs = singlePlaceCNOTs(
        definingObject.value.decodeE[Int, Map[Int, Bit]](nOracleQubits).collect {
          case (i, One(_)) => i -> nOracleQubits
        }
      )
      val circuit = Circuit(cNOTs: _*)
      circuit
  }
}
