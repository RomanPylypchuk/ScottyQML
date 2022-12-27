package qroutines.instances.procedures

import cats.data.{Reader, ValidatedNec}
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import qroutines.blocks.procedure.QuantumProcedure
import qroutines.blocks.routine.QuantumRoutineOutput.LongOutput
import qroutines.instances.procedures.ProcedureStep.{NoQuantum, WithQuantum}
import qroutines.instances.routines.qft.NShor
import quantumroutines.qft.ModularUnitaryParams
import utils.algebra.NumberTheoryRoutines.gcd
import quantumroutines.blocks.CircuitParams.OrderFindingParams

object ShorProcedure extends QuantumProcedure[ModularUnitaryParams] {

  //TODO - perhaps use Lens for this
  val checkEven: Reader[ModularUnitaryParams, ValidatedNec[String, ModularUnitaryParams]] = Reader {
    case params@ModularUnitaryParams(_, n) =>
      if (n % 2 == 0) params.copy(N = 2L).validNec
      else "Not a factor of 2".invalidNec
  }

  val checkPowerOf: Reader[ModularUnitaryParams, ValidatedNec[String, ModularUnitaryParams]] = Reader {
    _ => "Not a power of a".invalidNec
  }
  //TODO - Check if n = a^b for some a,b; Always fails for now

  val checkGCD: Reader[ModularUnitaryParams, ValidatedNec[String, ModularUnitaryParams]] = Reader {
    case params@ModularUnitaryParams(x, n) =>
      val (common, _) = gcd(x, n)
      if (common > 1) params.copy(N = common).validNec else s"GCD($x, $n) is one".invalidNec
  }

  type Routine = NShor.type
  val routine: NShor.type = NShor

  val findViaOrder: WithQuantum[ModularUnitaryParams, OrderFindingParams] =
    WithQuantum[ModularUnitaryParams, OrderFindingParams](
      (a, inParams) => inParams.copy(modParams = a),
      { case (modParams, LongOutput(factor)) => modParams.copy(N = factor) }
    )

  val steps: List[ProcedureStep[ModularUnitaryParams, OrderFindingParams]] = List(
    NoQuantum(checkEven),
    NoQuantum(checkPowerOf),
    NoQuantum(checkGCD),
    findViaOrder
  )
}
