package qroutines.instances.routines.qft

import qroutines.blocks.routine.QuantumRoutine
import quantumroutines.blocks.CircuitParams.{QPEParams, OrderFindingParams}
import qroutines.instances.circuits.OrderFindingCircuit
import cats.data.Reader
import quantumroutines.blocks.CircuitParams
import qroutines.instances.interpreters.ShorInterpreter

trait NShor extends QuantumRoutine{
  
    type InParamsType = OrderFindingParams
    type RoutineCircuitType = OrderFindingCircuit.type

    val qrCircuit: OrderFindingCircuit.type = OrderFindingCircuit
    val qrMeasureQubits: Reader[QPEParams, Set[Int]] = Reader{qpeParams => (0 until qpeParams.qubits.nPhaseQubits.nQubits).toSet}
    val qrInterpreter: ShorInterpreter.type = ShorInterpreter
}
