package qroutines.blocks.routine

import qroutines.blocks.routine.QuantumRoutineCircuit.DependentQuantumRoutineCircuit

trait QuantumDependentRoutine extends QuantumRoutine{
  type RoutineCircuitType <: DependentQuantumRoutineCircuit
}
