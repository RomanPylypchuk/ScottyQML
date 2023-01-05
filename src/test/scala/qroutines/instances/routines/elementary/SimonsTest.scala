package qroutines.instances.routines.elementary

import cats.data.ValidatedNec
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import org.scalatest.flatspec.AnyFlatSpec
import qroutines.blocks.measurements.QuantumMeasurementBackend.DefaultScottyBackend
import qroutines.blocks.noracle.OracleDefinitions.BitStringValue
import qroutines.blocks.routine.QuantumRoutine
import qroutines.blocks.routine.QuantumRoutineOutput.{OneOrTwoToOne, OneToOne, TwoToOne}
import qroutines.instances.oracles.NSimonsOracle
import qroutines.instances.oracles.NSimonsOracle.{NIdentityOracle, NRandomOneToOneOracle, TwoToOneOracle}
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.BitRegister
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister

class SimonsTest extends AnyFlatSpec{

  val runSimons: NSimonsOracle => ValidatedNec[String, OneOrTwoToOne] = { sOracle =>
    QuantumRoutine.run(2000, DefaultScottyBackend)(NSimons(sOracle))(NumberQubits(6))}

  //TODO - first two OneToOne tests are rather probabilistic
  "Simons output for Identity Oracle" should "be OneToOne" in {
    assert(runSimons(NIdentityOracle) == OneToOne.validNec)
  }

  "Simons output for random OneToOne Oracle" should "be OneToOne" in {
    assert(runSimons(NRandomOneToOneOracle) == OneToOne.validNec)
  }

  "Simons output for two to one b=110" should "be TwoToOne(110)" in {
    val b = "110"
    val twoToOneOracle = TwoToOneOracle(BitStringValue(b.encode[BitRegister]))
    assert(runSimons(twoToOneOracle) == TwoToOne(b.reverse.encode[BitRegister]).validNec)
  }
}
