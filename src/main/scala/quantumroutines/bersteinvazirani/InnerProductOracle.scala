package quantumroutines.bersteinvazirani

import quantumroutines.oracle.{HasBitString, Oracle}
import scotty.quantum.{Bit, BitRegister, Circuit, One}
import utils.BitRegisterFactory.controlMapBitRegister
import utils.codec.BiCodec.BiCodecSyntax
import utils.singlePlaceCNOTs


case class InnerProductOracle(nOracleQubits: Int, bitString: BitRegister) extends Oracle with HasBitString{
  def oracle: Circuit = {
    val cNOTs = singlePlaceCNOTs(bitString.decodeE[Int, Map[Int, Bit]](nOracleQubits).collect{case (i, One(_)) => i -> nOracleQubits})
    Circuit(cNOTs :_*)
  }
}
