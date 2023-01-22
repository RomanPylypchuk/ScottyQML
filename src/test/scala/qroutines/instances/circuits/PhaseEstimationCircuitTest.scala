package qroutines.instances.circuits

import cats.implicits.catsSyntaxValidatedIdBinCompat0
import org.scalatest.flatspec.AnyFlatSpec
import qroutines.blocks.ControlledUnitaryPower
import qroutines.blocks.routine.QuantumRoutine
import qroutines.blocks.routine.QuantumRoutineOutput.RationalOutput
import qroutines.instances.routines.qft.NPhaseEstimation
import quantumroutines.blocks.CircuitParams.{NumberQubits, QPEParams, QPEQubits}
import scotty.quantum.Circuit
import scotty.quantum.gate.Controlled
import scotty.quantum.gate.StandardGate.{PHASE, X}
import spire.math.Rational
import utils.algebra.precision.Precision
import utils.algebra.precision.Precision.AlmostEquals

class PhaseEstimationCircuitTest extends AnyFlatSpec{

  implicit val precision: Precision = Precision(1e-1)
  val qpeQubits: QPEQubits = QPEQubits(NumberQubits(3), NumberQubits(1))
  val eigenPrep: Circuit = Circuit(X(3))

  "Phase estimate for T gate" should "be 1/8" in {
    val sGen: ControlledUnitaryPower = ControlledUnitaryPower(
      (ci, j) => Circuit(Controlled(ci, PHASE((Math.PI / 4) * math.pow(2, j).toInt, 3)))
    )
    val qpeParams = QPEParams(qpeQubits, eigenPrep, sGen)
    val estimate = QuantumRoutine.run(2000)(NPhaseEstimation)(qpeParams)
    assert(estimate == RationalOutput(Rational(1,8)).validNec)
  }

  "Phase estimate for 1/3 gate" should "be close to 1/3" in {
    val sGen: ControlledUnitaryPower = ControlledUnitaryPower(
      (ci, j) => Circuit(Controlled(ci, PHASE(2 * (Math.PI / 3) * math.pow(2, j).toInt, 3)))
    )
    val qpeParams = QPEParams(qpeQubits, eigenPrep, sGen)
    val estimate = QuantumRoutine.run(2000)(NPhaseEstimation)(qpeParams)
    assert(estimate.map(_.x.toDouble ~= 0.333) == true.validNec)
  }
}
