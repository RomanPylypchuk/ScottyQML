package quantumroutines.combined
import cats.data.Reader
import quantumroutines.blocks.CircuitParams.NoParams
import quantumroutines.blocks.{CircuitParams, CircuitWithParams}
import scotty.quantum.Circuit

object DummyCircuit extends QuantumRoutineCircuit {
  type InParamsType = NoParams.type
  type UsedRoutineType = this.type
  type OutParamsType = NoParams.type

  val usedRoutine: Option[DummyCircuit.type] = None
  val inParamsToUsedRoutineParams: Option[InParamsType => DummyCircuit.type] = None
  val circuit: Reader[InParamsType, CircuitWithParams[OutParamsType]] =
    Reader(_ => CircuitWithParams(Circuit(), NoParams))
  val inverse: Reader[CircuitParams.NoParams.type, CircuitWithParams[CircuitParams.NoParams.type]] = circuit
}
