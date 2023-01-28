package qroutines.blocks.procedure

import cats.data.{Reader, ValidatedNec}
import qroutines.blocks.CircuitParams
import qroutines.blocks.routine.QuantumRoutineOutput


sealed trait ProcedureStep[A, +P <: CircuitParams]{
  //val step: Reader[A, ValidatedNec[String, A]]
}

object ProcedureStep{
  case class NoQuantum[A](step: Reader[A, ValidatedNec[String, A]]) extends ProcedureStep[A, Nothing]
  case class WithQuantum[A, P <: CircuitParams](modifyInParams: (A, P) => P, modifyOut: (A, QuantumRoutineOutput) => A)
    extends ProcedureStep[A, P]

}
