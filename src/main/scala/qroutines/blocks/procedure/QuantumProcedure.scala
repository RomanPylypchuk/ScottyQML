package qroutines.blocks.procedure

import cats.data.Validated._
import cats.data.{Kleisli, Reader, ValidatedNec, Writer}
import cats.implicits.{catsKernelStdMonoidForVector, catsSyntaxValidatedIdBinCompat0}
import qroutines.blocks.measurements.QuantumMeasurementBackend.DummyBackend
import qroutines.blocks.routine.{QuantumRoutine, QuantumRoutineOutput}
import qroutines.instances.procedures.ProcedureStep
import qroutines.instances.procedures.ProcedureStep.{NoQuantum, WithQuantum}


trait QuantumProcedure[A] {

  //type Routine <: QuantumRoutine

  type Step = Reader[A, ValidatedNec[String, A]]
  type Arrow = Kleisli[Writer[Vector[String], *], Either[A, A], Either[A, A]]

  val routine: QuantumRoutine
  val steps: List[ProcedureStep[A, routine.InParamsType]]
  val mapRoutineOutput: QuantumRoutineOutput => A //TODO - somehow conform Routine output type to A, instead of this

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

  val procedure: Reader[routine.qrCircuit.InParamsType, Kleisli[Writer[Vector[String], *], Either[A, A], Either[A, A]]] =
    Reader { inParams =>
      val kleislis: List[Arrow] = steps.map {

        case NoQuantum(step) => stepToKleisli(step)

        case WithQuantum(modParams) =>
          val qStep = Reader[A, ValidatedNec[String, A]] {
            a =>
              val newParams: routine.InParamsType = modParams(a, inParams)
              val rOutput = QuantumRoutine.run(routine)(1000)(newParams)(DummyBackend) //TODO - factor out measurement params
              rOutput.map(mapRoutineOutput)
          }
          stepToKleisli(qStep)
      }
      kleislis.reduceLeft[Arrow](_ compose _)
    }
}

object QuantumProcedure {

  def run[A](init: A, qp: QuantumProcedure[A])(inParams: qp.routine.InParamsType): (Vector[String], Either[A, A]) = {
    val writer = qp.procedure(inParams)(Left(init))
    writer.run
  }

  val checkEven: Long => ValidatedNec[String, Long] = { n => println("Checking even..."); if (n % 2 == 0) 2L.validNec else "Not a factor of 2".invalidNec }
  val checkPowerOf: Long => ValidatedNec[String, Long] = { _ => println("Checking power..."); "Not a power of a".invalidNec }

  //val init: Writer[Vector[String], Either[Long, Long]] = Writer(Vector.empty[String], 1877L.asLeft[Long])
  //  init.flatMap(stepToKleisli(Reader(checkEven)).run)
}
