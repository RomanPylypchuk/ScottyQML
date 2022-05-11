package quantumroutines.bersteinvazirani

import scotty.quantum.ExperimentResult.StateStats
import scotty.quantum.gate.StandardGate.{H, X}
import scotty.quantum.{BitRegister, Circuit}
import utils.HTensor
import utils.Measure.{StateStatsOps, measureTimes}

object BernsteinVazirani{

  def bernsteinVazirani(function: InnerProductOracle): Circuit ={
    //TODO: Next lines - exactly the same as in deutschJosza, but with different function, perhaps abstract
    val nQubits = function.nOracleQubits + 1
    val left: Circuit = Circuit.apply(Circuit.generateRegister(nQubits), X(nQubits - 1), H(nQubits - 1))
    val oracleHadamards: Circuit = Circuit(HTensor(nQubits - 1) :_*)
    left combine oracleHadamards combine function.oracle combine oracleHadamards
  }

  def runBernsteinVazirani(function: InnerProductOracle): BitRegister ={

    val bv: Circuit = bernsteinVazirani(function)

    val allQubitsStats: StateStats = measureTimes(1000)(bv)
    val neededQubitsStats: StateStats = allQubitsStats.exceptQubits(Set(function.nOracleQubits))
    neededQubitsStats.stats.maxBy{case (_, times) => times}._1
  }

}
