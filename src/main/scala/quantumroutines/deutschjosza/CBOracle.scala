package quantumroutines.deutschjosza

import quantumroutines.Oracle
import scotty.quantum.gate.StandardGate.X
import scotty.quantum.{Bit, Circuit, One, Zero}
import utils.BitRegisterFactory._
import utils.singlePlaceCNOTs

sealed trait CBOracle extends Oracle{
  type oracleType <: ConstantOrBalanced
  def nOracleQubits: Int  //Number of input qubits to oracle
  def oracle: Circuit //Perhaps refactor this as a function outside of Oracle, leaving it as pure ADT?
}

object CBOracle{

  sealed trait ConstantOracle extends CBOracle {
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

  //TODO: Could use type [A] and implicit `BitRegisterFrom` to instantiate input X configuration e.g. from String, Map[Int, Bit], etc.
  final case class BalancedOracle(nOracleQubits: Int, balanceShift: Option[Map[Int, Bit]] = None) extends CBOracle {
    type oracleType = Balanced.type

    def oracle: Circuit = {
      val shift: Option[Circuit] = balanceShift.map(cMap => (nOracleQubits, cMap).toBitRegister.toCircuit)

      val cNOTs = singlePlaceCNOTs((0 until nOracleQubits).map(_ -> nOracleQubits).toMap)
      val balancedInside: Circuit = Circuit(cNOTs: _*)

      shift.fold(balancedInside)(sCircuit => sCircuit combine balancedInside combine sCircuit)
    }
  }

}