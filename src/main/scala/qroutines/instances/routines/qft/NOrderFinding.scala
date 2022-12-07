package qroutines.instances.routines.qft

import cats.data.Reader

import quantumroutines.blocks.CircuitParams.{QPEParams, OrderFindingParams}
import qroutines.instances.circuits.OrderFindingCircuit
import qroutines.instances.interpreters.OrderFindingInterpreter
import qroutines.blocks.routine.QuantumRoutine

object NOrderFinding extends QuantumRoutine{
 type InParamsType = OrderFindingParams
 type RoutineCircuitType = OrderFindingCircuit.type

val qrCircuit: RoutineCircuitType = OrderFindingCircuit
val qrMeasureQubits: Reader[QPEParams, Set[Int]] = Reader{qpeParams => (0 until qpeParams.qubits.nPhaseQubits.nQubits).toSet}
val qrInterpreter: OrderFindingInterpreter.type = OrderFindingInterpreter
}
