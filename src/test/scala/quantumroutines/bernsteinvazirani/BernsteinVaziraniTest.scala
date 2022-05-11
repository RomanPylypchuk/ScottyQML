package quantumroutines.bernsteinvazirani

import quantumroutines.bersteinvazirani.BernsteinVazirani.runBernsteinVazirani
import quantumroutines.bersteinvazirani.InnerProductOracle
import utils.BitRegisterFactory.BitRegisterFrom

object BernsteinVaziraniTest extends App{
  val oracleBinary = "011".toBitRegister
  val iOracle = InnerProductOracle(3, oracleBinary)
  assert(runBernsteinVazirani(iOracle) == oracleBinary)
}
