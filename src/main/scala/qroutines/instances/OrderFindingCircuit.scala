package qroutines.instances

import breeze.numerics.log2
import cats.data.Reader
import qroutines.QuantumRoutineCircuit.DependentQuantumRoutineCircuit
import quantumroutines.blocks.CircuitParams.{NumberQubits, OrderFindingParams, QPEParams, QPEQubits}
import quantumroutines.blocks.CircuitWithParams
import scotty.quantum.Circuit
import scotty.quantum.gate.StandardGate.X


object OrderFindingCircuit extends DependentQuantumRoutineCircuit{
  type InParamsType = OrderFindingParams
  type UsedRoutineType = PhaseEstimationCircuit.type
  type OutParamsType = QPEParams

  val usedRoutine: PhaseEstimationCircuit.type = PhaseEstimationCircuit
  val inParamsToUsedRoutineParams: Reader[OrderFindingParams, QPEParams] = Reader{
    ofParams =>
      val modUParams = ofParams.modParams
      val nEigenQubits: Int = log2(modUParams.N.toDouble).ceil.toInt
      val nPhaseQubits: Int = 2 * nEigenQubits + 1
      val eigenStatePrep: Circuit = Circuit(X(nPhaseQubits + nEigenQubits - 1))
      val qpeQubits = QPEQubits(NumberQubits(nPhaseQubits), NumberQubits(nEigenQubits))
      QPEParams(qpeQubits, eigenStatePrep ,ofParams.modExp.controlPower(modUParams))
  }

  val circuit: Reader[OrderFindingParams, CircuitWithParams[QPEParams]] =
    usedRoutine.circuit compose inParamsToUsedRoutineParams

}
