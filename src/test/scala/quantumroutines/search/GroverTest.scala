package quantumroutines.search

import quantumroutines.oracle.Oracle
import scotty.quantum.Circuit
import scotty.quantum.gate.StandardGate.{CZ, H}
import utils.Measure.{StateStatsOps, measureTimes}

object GroverTest extends App{

  val diffuserTest = Grover.diffuser(4)
  val init = Circuit(H(0), H(1), H(2), H(3))
  println(diffuserTest)
  val results = measureTimes(1000)(init combine diffuserTest)
  println(results) //Should be still uniform

  //////////////////////////////////////////

  //Oracle for w = |11>
  val test2Oracle = new Oracle{
    def nOracleQubits: Int = 2
    def oracle: Circuit = Circuit(CZ(0, 1))
  }

  val grover2 = Grover.grover(1)(test2Oracle)
  println(grover2)
  val z = measureTimes(1000)(grover2).exceptQubits(Set(2))
  z.stats.foreach(println)
}
