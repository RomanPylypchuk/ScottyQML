package quantumroutines.bersteinvazirani

import quantumroutines.Oracle
import scotty.quantum.{BitRegister, Circuit, One}
import utils.BitRegisterFactory._
import utils.singlePlaceCNOTs

case class InnerProductOracle(nOracleQubits: Int, binary: BitRegister) extends Oracle{
  def oracle: Circuit = {
    val cNOTs = singlePlaceCNOTs(binary.toControlMap.collect{case (i, One(_)) => i -> nOracleQubits})
    Circuit(cNOTs :_*)
  }
}
