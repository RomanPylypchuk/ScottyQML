package qroutines.instances.procedures

import cats.data.{Reader, ValidatedNec}
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import qroutines.blocks.procedure.QuantumProcedure
import qroutines.blocks.routine.QuantumRoutine
import qroutines.instances.procedures.ProcedureStep.{NoQuantum, WithQuantum}
import qroutines.instances.routines.qft.NShor
import quantumroutines.qft.ModularUnitaryParams
import utils.algebra.NumberTheoryRoutines.gcd

//Must be object
trait ShorProcedure extends QuantumProcedure[ModularUnitaryParams]{

  val checkEven: Reader[ModularUnitaryParams, ValidatedNec[String, Long]] = Reader{
    case ModularUnitaryParams(_, n) =>
      if (n % 2 == 0) 2L.validNec else "Not a factor of 2".invalidNec
  }

  val checkPowerOf: Reader[ModularUnitaryParams, ValidatedNec[String, Long]] = Reader{
    _ => "Not a power of a".invalidNec }
  //TODO - Check if n = a^b for some a,b; Always fails for now

  val checkGCD: Reader[ModularUnitaryParams, ValidatedNec[String, Long]] = Reader{
    case ModularUnitaryParams(x, n) =>
    val (common, _) = gcd(x, n)
    if (common > 1) common.validNec else s"GCD($x, $n) is one".invalidNec
  }

  type Routine = NShor.type
  val routine: NShor.type = NShor
/*
  val steps: List[ProcedureStep[ModularUnitaryParams]] = List(
    NoQuantum(checkEven),
    NoQuantum(checkPowerOf),
    NoQuantum(checkGCD),
    WithQuantum{shor =>
      shor.run(1000)()
    }
  )

 */
}
