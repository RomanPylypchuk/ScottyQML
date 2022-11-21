package qroutines

import cats.data.Reader
import quantumroutines.blocks.CircuitParams.NoParams
import quantumroutines.blocks.{CircuitParams, CircuitWithParams}
import scotty.quantum.Circuit
import utils.GateUtils.InverseCircuit

trait QuantumRoutineCircuit {
  type InParamsType <: CircuitParams
  type OutParamsType <: CircuitParams

  val circuit: Reader[InParamsType, CircuitWithParams[OutParamsType]]

  val inverse: Reader[InParamsType, CircuitWithParams[OutParamsType]] =
    circuit.map(cwP => cwP.copy(circuit = cwP.circuit.dagger))
}

object QuantumRoutineCircuit{

  trait IndependentQuantumRoutineCircuit extends QuantumRoutineCircuit

  object EmptyCircuit extends IndependentQuantumRoutineCircuit {
    type InParamsType = NoParams.type
    type OutParamsType = NoParams.type

    val circuit: Reader[InParamsType, CircuitWithParams[OutParamsType]] =
      Reader(_ => CircuitWithParams(Circuit(), NoParams))

  }

  trait DependentQuantumRoutineCircuit extends QuantumRoutineCircuit{

    type UsedRoutineType <: QuantumRoutineCircuit

    val usedRoutine: UsedRoutineType
    val inParamsToUsedRoutineParams: Reader[InParamsType, usedRoutine.InParamsType]
  }
}
