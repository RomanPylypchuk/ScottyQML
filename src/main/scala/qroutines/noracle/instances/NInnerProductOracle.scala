package qroutines.noracle.instances

import cats.data.Reader
import qroutines.noracle.NOracle
import qroutines.noracle.OracleDefinitions.BitStringValue
import quantumroutines.blocks.CircuitParams.NumberQubits
import quantumroutines.blocks.CircuitWithParams
import scotty.quantum.{Bit, Circuit, One}
import utils.GateUtils.singlePlaceCNOTs
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.controlMapBitRegister

case class NInnerProductOracle(definingObject: BitStringValue) extends NOracle{
  type DefiningType = BitStringValue

  val circuit: Reader[NumberQubits, CircuitWithParams[NumberQubits]] = Reader{
    case nq @ NumberQubits(nOracleQubits) =>
    val cNOTs = singlePlaceCNOTs(
      definingObject.value.decodeE[Int, Map[Int, Bit]](nOracleQubits).collect{
        case (i, One(_)) => i -> nOracleQubits}
    )
    val circuit = Circuit(cNOTs :_*)
    CircuitWithParams(circuit, nq)
  }
}
