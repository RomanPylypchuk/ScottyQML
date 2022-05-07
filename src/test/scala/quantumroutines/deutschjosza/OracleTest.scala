package quantumroutines.deutschjosza

import scotty.quantum.gate.StandardGate.X
import scotty.quantum.gate.TargetGate
import scotty.quantum.gate.TargetGate.MatrixGen
import scotty.quantum.{Circuit, One}

object OracleTest extends App{
  val zeroOracle = Oracle.ZeroOracle(3)
  println(zeroOracle.oracle)

  val oneOracle = Oracle.OneOracle(3)
  println(oneOracle.oracle)

  val balancedOracle1 = Oracle.BalancedOracle(3)
  println(balancedOracle1.oracle)

  val balancedOracle2 = Oracle.BalancedOracle(3, Some(Map(0 -> One(), 2 -> One())))
  println(balancedOracle2.oracle)

  val xMod = X(0).copy(index = 4)
  println(xMod)

  def placeGate(indices: List[Int], gate: TargetGate): Circuit = {
    val modGate = new TargetGate {
      val index: Int = 2
      val params: scala.Seq[Double] = gate.params
      val matrixGen: MatrixGen = gate.matrixGen
    }
    Circuit(modGate)
  }

  println(placeGate(List(1), X(0)))
}
