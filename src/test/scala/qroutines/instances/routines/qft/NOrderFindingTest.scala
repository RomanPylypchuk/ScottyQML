package qroutines.instances.routines.qft

import cats.implicits.catsSyntaxValidatedIdBinCompat0
import org.scalatest.flatspec.AnyFlatSpec
import qroutines.blocks.CircuitParams.OrderFindingParams
import qroutines.blocks.modular.ModularUnitaryParams
import qroutines.blocks.routine.QuantumRoutine
import qroutines.blocks.routine.QuantumRoutineOutput.LongOutput
import scotty.quantum.gate.StandardGate.X
import scotty.quantum.{BitRegister, Circuit}
import utils.Measure.measureTimes
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.decimalBitRegister


class NOrderFindingTest extends AnyFlatSpec{

  val modularParams: ModularUnitaryParams = ModularUnitaryParams(7, 15)

  "Quantum modular exponentiation 4^(2^j) mod 15 using 4 qubits" should "give results, identical to classically computed" in {

    val modFifteenResults = (0 to 7).map { jPower =>
      println("Calculating 4^" + math.pow(2, jPower).toInt + "(mod 15) quantum mechanically...")
      val (resultModExpBinary, _) =
        measureTimes(1000)(Circuit(X(3), modFifteenFourEigenQubits(0)(4)(jPower))).stats.filter { case (_, times) => times != 0 }.head
      val resultModExpDecimal = BitRegister(resultModExpBinary.values.reverse: _*).decodeE[Int, Int](4)
      math.pow(4, math.pow(2, jPower).toInt) % 15 -> (resultModExpBinary, resultModExpDecimal)
    }

    assert(modFifteenResults.forall { case (expDecimal, (_, qmDecimal)) => expDecimal == qmDecimal })
  }

  "Order of 7 mod 15, computed quantum mechanically" should "give order r=4" in {
    val orderFindingParams = OrderFindingParams(modularParams, mod15)
    val result = QuantumRoutine.run(50)(NOrderFinding)(orderFindingParams)
    assert(result == LongOutput(4L).validNec)
  }
}
