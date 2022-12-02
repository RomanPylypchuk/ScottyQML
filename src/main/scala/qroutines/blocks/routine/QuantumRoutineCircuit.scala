package qroutines.blocks.routine

import cats.data.Reader
import quantumroutines.blocks.CircuitParams.NoParams
import quantumroutines.blocks.{CircuitParams, CircuitWithParams}
import scotty.quantum.Circuit
import utils.GateUtils.InverseCircuit

trait QuantumRoutineCircuit {
  type InParamsType <: CircuitParams

  val circuit: Reader[InParamsType, Circuit]

  val inverse: Reader[InParamsType, Circuit] =
    circuit.map(circuit => circuit.dagger)
}

object QuantumRoutineCircuit{

  trait IndependentQuantumRoutineCircuit extends QuantumRoutineCircuit

  object EmptyCircuit extends IndependentQuantumRoutineCircuit {
    type InParamsType = NoParams.type

    val circuit: Reader[InParamsType, Circuit] =
      Reader(_ => Circuit())

  }

  trait DependentQuantumRoutineCircuit extends QuantumRoutineCircuit{

    type UsedRoutineType <: QuantumRoutineCircuit

    val usedRoutine: UsedRoutineType
    val inParamsToUsedRoutineParams: Reader[InParamsType, usedRoutine.InParamsType]
  }
}
