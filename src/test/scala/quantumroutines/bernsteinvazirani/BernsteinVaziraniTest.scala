package quantumroutines.bernsteinvazirani

import quantumroutines.bersteinvazirani.BernsteinVazirani.runBernsteinVazirani
import quantumroutines.bersteinvazirani.InnerProductOracle
import scotty.quantum.BitRegister
import utils.BitRegisterFactory.stringBitRegister
import utils.codec.BiCodec.BiCodecSyntax

object BernsteinVaziraniTest extends App{
  val oracleBinary = "011".encode[BitRegister]
  val iOracle = InnerProductOracle(3, oracleBinary)
  assert(runBernsteinVazirani(iOracle) == oracleBinary)
}
