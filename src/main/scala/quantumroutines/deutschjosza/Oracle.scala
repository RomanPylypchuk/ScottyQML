package quantumroutines.deutschjosza

import scotty.quantum.gate.StandardGate.{CNOT, X}
import scotty.quantum.{Bit, Circuit, One, Zero}
import utils.BitRegisterFactory._

sealed trait Oracle {
  type oracleType <: ConstantOrBalanced
  def nOracleQubits: Int  //Number of input qubits to oracle
  def oracle: Circuit
}

object Oracle{

  sealed trait ConstantOracle extends Oracle {
    type oracleType = Constant.type

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

  //Could use type [A] and implicit `BitRegisterFrom` to instantiate input X configuration e.g. from String, Map[Int, Bit], etc.
  final case class BalancedOracle(nOracleQubits: Int, balanceShift: Option[Map[Int, Bit]] = None) extends Oracle {
    type oracleType = Balanced.type

    def oracle: Circuit = {
      val shift: Option[Circuit] = balanceShift.map(cMap => (nOracleQubits, cMap).toBitRegister.toCircuit)

      val cNOTs = (0 until nOracleQubits).map(CNOT(_, nOracleQubits))
      val balancedInside: Circuit = Circuit(cNOTs: _*)

      shift.fold(balancedInside)(sCircuit => sCircuit combine balancedInside combine sCircuit)
    }
  }

}