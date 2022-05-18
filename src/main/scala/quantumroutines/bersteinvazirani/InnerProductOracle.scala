package quantumroutines.bersteinvazirani

import quantumroutines.Oracle
import scotty.quantum.{Bit, BitRegister, Circuit, One}
import utils.BitRegisterFactory.controlMapBitRegister
import utils.codec.BiCodec.BiCodecSyntax
import utils.singlePlaceCNOTs


case class InnerProductOracle(nOracleQubits: Int, binary: BitRegister) extends Oracle{
  def oracle: Circuit = {
    val cNOTs = singlePlaceCNOTs(binary.decodeE[Int, Map[Int, Bit]](nOracleQubits).collect{case (i, One(_)) => i -> nOracleQubits})
    Circuit(cNOTs :_*)
  }
}
