package qroutines.instances.interpreters

import cats.implicits.catsSyntaxValidatedIdBinCompat0
import org.scalatest.flatspec.AnyFlatSpec
import qroutines.blocks.NModularExponentiation
import qroutines.blocks.routine.QuantumRoutineOutput.LongOutput
import quantumroutines.blocks.CircuitParams.OrderFindingParams
import quantumroutines.qft.ModularUnitaryParams

class ShorInterpreterTest extends AnyFlatSpec{

  val modParams: ModularUnitaryParams = ModularUnitaryParams(7, 15)

  "Prime factor from order r=4 for (x=7, N=15)" should "result in 5" in {
    val ofParams = OrderFindingParams(modParams, NModularExponentiation.DummyExponentiation)
    val fromOrder = ShorInterpreter.factorFromOrder(ofParams)
    assert(fromOrder(LongOutput(4L)) == LongOutput(5L).validNec)
  }
}
