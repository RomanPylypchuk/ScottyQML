package qroutines

import cats.data.Reader
import quantumroutines.blocks.CircuitParams.NoParams
import quantumroutines.blocks.{CircuitParams, CircuitWithParams}
import scotty.quantum.Circuit

trait QuantumRoutineCircuit {
  type InParamsType <: CircuitParams
  type OutParamsType <: CircuitParams

  val circuit: Reader[InParamsType, CircuitWithParams[OutParamsType]]

  //TODO - implement inverse
  //val inverse: Reader[InParamsType, CircuitWithParams[OutParamsType]]
  //Dagger from Scotty is only applied to TargetGate, need to generalize to control gates, etc
  /*
    circuit.map{circWParams =>
    val revGates = circWParams.circuit.gates.reverse.map{
      case x => Dagger(x)
    }
    //Circuit(revGates :_*)
  }*/
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
    val inParamsToUsedRoutineParams: InParamsType => usedRoutine.InParamsType
  }
}
