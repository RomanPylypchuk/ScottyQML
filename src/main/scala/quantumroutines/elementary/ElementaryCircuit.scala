package quantumroutines.elementary

import quantumroutines.oracle.{Oracle, OracleOutput}
import scotty.quantum.Circuit
import scotty.quantum.ExperimentResult.StateStats
import utils.Measure.{StateStatsOps, measureTimes}

//TODO - Or perhaps use typeclass pattern for this?
trait ElementaryCircuit {
  type CircuitOracle <: Oracle
  type CircuitOutput <: OracleOutput

  def preOracle: Int => Circuit
  def postOracle: Int => Circuit

  //CircuitOracle seems like E for Reader[E,A]
  def interpret(oracle: CircuitOracle): StateStats => CircuitOutput

  val circuit: CircuitOracle => Circuit =
    oracle =>
      preOracle(oracle.nOracleQubits) combine oracle.oracle combine postOracle(oracle.nOracleQubits)
}

object ElementaryCircuit {

  implicit class ElementaryRunner[Q <: ElementaryCircuit](val ec: Q){
    def run(times: Int)(oracle: ec.CircuitOracle): ec.CircuitOutput = {
      val circuit: Circuit = ec.circuit(oracle)
      val neededStats: StateStats = measureTimes(times)(circuit).forQubits((0 until oracle.nOracleQubits).toSet)
      //Map results to the desired output of the algorithm
      ec.interpret(oracle)(neededStats)
    }
  }

}