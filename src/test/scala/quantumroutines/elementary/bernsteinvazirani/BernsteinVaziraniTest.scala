package quantumroutines.elementary.bernsteinvazirani

import quantumroutines.elementary.algorithms.BernsteinVazirani
import quantumroutines.oracle.instances.InnerProductOracle
import scotty.quantum.BitRegister
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister

object BernsteinVaziraniTest extends App{
  val oracleBinary = "001".encode[BitRegister]
  val iOracle = InnerProductOracle(3, oracleBinary)
  val oracleBitString = BernsteinVazirani.run(1000)(iOracle)
  assert(BitRegister(oracleBitString.b.values.reverse :_*) == oracleBinary)
}
