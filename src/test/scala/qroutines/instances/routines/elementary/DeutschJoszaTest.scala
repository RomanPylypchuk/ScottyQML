package qroutines.instances.routines.elementary

import cats.data.ValidatedNec
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import org.scalatest.flatspec.AnyFlatSpec
import qroutines.blocks.measurements.QuantumMeasurementBackend.DefaultScottyBackend
import qroutines.blocks.noracle.OracleDefinitions.BitShiftValue
import qroutines.blocks.routine.QuantumRoutine
import qroutines.blocks.routine.QuantumRoutineOutput._
import qroutines.instances.oracles.NDeutschJoszaOracle
import qroutines.instances.oracles.NDeutschJoszaOracle._
import quantumroutines.blocks.CircuitParams.NumberQubits

class DeutschJoszaTest extends AnyFlatSpec {

  val runDeutschJosza: NDeutschJoszaOracle => ValidatedNec[String, ConstantOrBalanced] = { djOracle =>
    QuantumRoutine.run(1000, DefaultScottyBackend)(NDeutschJosza(djOracle))(NumberQubits(4))}

  "Deutsch Josza output for Zero Oracle" should "be Constant" in {
    assert(runDeutschJosza(ZeroOracle) == Constant.validNec)
  }

  "Deutsch Josza output for One Oracle" should "be Constant" in {
    assert(runDeutschJosza(OneOracle) == Constant.validNec)
  }

  "Deutsch Josza output for Balanced Oracle" should "be Balanced" in {
    assert(runDeutschJosza(BalancedOracle(BitShiftValue(None))) == Balanced.validNec)
  }


}
