package quantumroutines.combined

import cats.data.Reader
import quantumroutines.blocks.{CircuitParams, CircuitWithParams}

trait QuantumRoutineCircuit {
  type InParamsType <: CircuitParams
  type UsedRoutineType <: QuantumRoutineCircuit
  type OutParamsType <: CircuitParams

  val usedRoutine: Option[UsedRoutineType]
  val inParamsToUsedRoutineParams: Option[InParamsType => UsedRoutineType]

  val circuit: Reader[InParamsType, CircuitWithParams[OutParamsType]]
  //val inverse: Reader[InParamsType, CircuitWithParams[OutParamsType]]
  //TODO - implement inverse
  //Dagger from Scotty is only applied to TargetGate, need to generalize to control gates, etc
  /*
    circuit.map{circWParams =>
    val revGates = circWParams.circuit.gates.reverse.map{
      case x => Dagger(x)
    }
    //Circuit(revGates :_*)
  }*/
}
