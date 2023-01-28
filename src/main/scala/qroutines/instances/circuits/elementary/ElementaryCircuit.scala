package qroutines.instances.circuits.elementary

import cats.data.Reader
import qroutines.blocks.CircuitParams.NumberQubits
import qroutines.blocks.noracle.Oracle
import qroutines.blocks.routine.QuantumRoutineCircuit.DependentQuantumRoutineCircuit
import scotty.quantum.Circuit

trait ElementaryCircuit extends DependentQuantumRoutineCircuit{

  type InParamsType = NumberQubits
  type OutParamsType = NumberQubits
  type UsedRoutineType <: Oracle

  val preOracle: Reader[NumberQubits, Circuit]
  val postOracle: Reader[NumberQubits, Circuit]

  val circuit: Reader[NumberQubits, Circuit] = Reader{
    nq =>
      val oracleCircuit: Circuit = usedRoutine.circuit(inParamsToUsedRoutineParams(nq))
      preOracle(nq) combine oracleCircuit combine postOracle(nq)
  }
}
