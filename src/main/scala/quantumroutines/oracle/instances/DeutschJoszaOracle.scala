package quantumroutines.oracle.instances

import quantumroutines.oracle.Oracle
import scotty.quantum._
import scotty.quantum.gate.StandardGate.X
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.{BitRegisterTo, controlMapBitRegister}
import utils.singlePlaceCNOTs

sealed trait DeutschJoszaOracle extends Oracle{
  //type OutputType = ScalarOutput
  //def nOracleQubits: Int  //Number of input qubits to oracle
  //def oracle: Circuit //Perhaps refactor this as a function outside of Oracle, leaving it as pure ADT?
}

object DeutschJoszaOracle{

  sealed trait ConstantOracle extends DeutschJoszaOracle {
    //type OutputType = Constant.type
    def output: Bit

    def oracle: Circuit =
      {
        val nQubits = nOracleQubits + 1
        val emptyCircuit = Circuit.apply(Circuit.generateRegister(nQubits))
        output match {
          case Zero(_) => emptyCircuit
          case One(_) => emptyCircuit combine Circuit(X(nQubits - 1))
        }
      }
  }

  final case class ZeroOracle(nOracleQubits: Int) extends ConstantOracle {
    def output: Bit = Zero()
  }

  final case class OneOracle(nOracleQubits: Int) extends ConstantOracle {
    def output: Bit = One()
  }

  //TODO: Could use type [A] and implicit `BitRegisterFrom` to instantiate input X configuration e.g. from String, Map[Int, Bit], etc.
  final case class BalancedOracle(nOracleQubits: Int, balanceShift: Option[Map[Int, Bit]] = None) extends DeutschJoszaOracle {
    //type OutputType = Balanced.type

    def oracle: Circuit = {
      val shift: Option[Circuit] = balanceShift.map(cMap => cMap.encodeE[Int, BitRegister](nOracleQubits).toCircuit)

      val cNOTs = singlePlaceCNOTs((0 until nOracleQubits).map(_ -> nOracleQubits).toMap)
      val balancedInside: Circuit = Circuit(cNOTs: _*)

      shift.fold(balancedInside)(sCircuit => sCircuit combine balancedInside combine sCircuit)
    }
  }

}