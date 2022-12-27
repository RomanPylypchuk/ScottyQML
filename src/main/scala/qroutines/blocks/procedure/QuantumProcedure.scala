package qroutines.blocks.procedure

import cats.data.Validated._
import cats.data.{Kleisli, Reader, ValidatedNec, Writer}
import cats.implicits.{catsKernelStdMonoidForVector, catsSyntaxValidatedIdBinCompat0}
import qroutines.blocks.measurements.QuantumMeasurementBackend
import qroutines.blocks.routine.QuantumRoutineOutput.LongOutput
import qroutines.blocks.routine.{QuantumRoutine, QuantumRoutineOutput}
import qroutines.instances.procedures.ProcedureStep
import qroutines.instances.procedures.ProcedureStep.{NoQuantum, WithQuantum}


trait QuantumProcedure[A] {

  type Step = Reader[A, ValidatedNec[String, A]]
  type Arrow = Kleisli[Writer[Vector[String], *], Either[A, A], Either[A, A]]

  val routine: QuantumRoutine
  type UsedRoutineOutput = routine.qrInterpreter.RoutineOutput
  val steps: List[ProcedureStep[A, routine.InParamsType]]

  val stepToKleisli: Step => Arrow =
    step =>
      Kleisli {
        case Left(uX) =>
          step(uX) match {
            case Valid(cX) => Writer(Vector("Successfully computed."), Right(cX))
            case Invalid(msg) => Writer(msg.toChain.toVector, Left(uX)) //TODO - fix slow conversions from NonEmptyChain to Vector
          }

        case done@Right(_) => Writer(Vector.empty[String], done)
      }

  val procedure: (Int, QuantumMeasurementBackend) => Reader[routine.qrCircuit.InParamsType, Arrow] =
    (times, backend) =>
    Reader { inParams =>
      val kleislis: List[Arrow] = steps.map {

        case NoQuantum(step) => stepToKleisli(step)

        case WithQuantum(modInParams, modOut) =>
          val qStep = Reader[A, ValidatedNec[String, A]] {
            a =>
              val newParams: routine.InParamsType = modInParams(a, inParams)
              val rOutput: ValidatedNec[String, UsedRoutineOutput] = QuantumRoutine.run[routine.type](times, backend)(routine)(newParams)
              rOutput.map(oValue => modOut(a, oValue))
          }
          stepToKleisli(qStep)
      }
      kleislis.reduceLeft[Arrow](_ compose _)
    }
}

object QuantumProcedure {

  //TODO - init and inParams may overlap in the beginning, not very nice to specify twice
  def run[A](times: Int, backend: QuantumMeasurementBackend)
      (qp: QuantumProcedure[A])(init: A)(inParams: qp.routine.InParamsType): (Vector[String], Either[A, A]) = {
    val writer = qp.procedure(times, backend)(inParams)(Left(init))
    writer.run
  }

  //val init: Writer[Vector[String], Either[Long, Long]] = Writer(Vector.empty[String], 1877L.asLeft[Long])
  //  init.flatMap(stepToKleisli(Reader(checkEven)).run)
}
