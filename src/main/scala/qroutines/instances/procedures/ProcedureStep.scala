package qroutines.instances.procedures

import cats.data.{Reader, ValidatedNec}
import qroutines.blocks.routine.QuantumRoutine
import quantumroutines.blocks.CircuitParams
import quantumroutines.blocks.CircuitParams.NoParams

sealed trait ProcedureStep[A, +P <: CircuitParams]{
  //val step: Reader[A, ValidatedNec[String, A]]
}

object ProcedureStep{
  case class NoQuantum[A](step: Reader[A, ValidatedNec[String, A]]) extends ProcedureStep[A, Nothing]
  case class WithQuantum[A, P <: CircuitParams](modifyParams: (A, P) => P) extends ProcedureStep[A, P]

}
