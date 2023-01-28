package qroutines.instances.routines.qft

import cats.data.Reader
import qroutines.blocks.CircuitParams.{OrderFindingParams, QPEParams}
import qroutines.blocks.routine.QuantumRoutine
import qroutines.instances.circuits.OrderFindingCircuit
import qroutines.instances.interpreters.ShorInterpreter

object NShor extends QuantumRoutine{
  
    type InParamsType = OrderFindingParams
    type RoutineCircuitType = OrderFindingCircuit.type

    val qrCircuit: OrderFindingCircuit.type = OrderFindingCircuit
    val qrMeasureQubits: Reader[QPEParams, Set[Int]] = Reader{qpeParams => (0 until qpeParams.qubits.nPhaseQubits.nQubits).toSet}
    val qrInterpreter: ShorInterpreter.type = ShorInterpreter
}
