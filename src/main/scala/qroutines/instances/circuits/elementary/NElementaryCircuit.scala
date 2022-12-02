package qroutines.instances.circuits.elementary

import cats.data.Reader
import qroutines.blocks.routine.QuantumRoutineCircuit.DependentQuantumRoutineCircuit
import qroutines.blocks.noracle.NOracle
import quantumroutines.blocks.CircuitParams.NumberQubits
import quantumroutines.blocks.CircuitWithParams
import scotty.quantum.Circuit

trait NElementaryCircuit extends DependentQuantumRoutineCircuit{

  type InParamsType = NumberQubits
  type OutParamsType = NumberQubits
  type UsedRoutineType <: NOracle

  val preOracle: Reader[NumberQubits, Circuit]
  val postOracle: Reader[NumberQubits, Circuit]

  val circuit: Reader[NumberQubits, Circuit] = Reader{
    nq =>
      val oracleCircuit: Circuit = usedRoutine.circuit(inParamsToUsedRoutineParams(nq))
      preOracle(nq) combine oracleCircuit combine postOracle(nq)
  }
}
