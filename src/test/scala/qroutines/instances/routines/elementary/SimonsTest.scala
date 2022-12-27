package qroutines.instances.routines.elementary

import cats.data.ValidatedNec
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import org.scalatest.flatspec.AnyFlatSpec
import qroutines.blocks.measurements.QuantumMeasurementBackend.DefaultScottyBackend
import qroutines.blocks.noracle.OracleDefinitions.BitStringValue
import qroutines.blocks.routine.QuantumRoutine
import qroutines.blocks.routine.QuantumRoutineOutput.{OneOrTwoToOne, OneToOne}
import qroutines.instances.oracles.NSimonsOracle
import qroutines.instances.oracles.NSimonsOracle.{NIdentityOracle, NRandomOneToOneOracle, TwoToOneOracle}
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.BitRegister
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister

class SimonsTest extends AnyFlatSpec{

  /*
  println(Simons.run(1000)(IdentityOracle(3)))

  val threeQubitTwoToOneOracle: SimonsOracle = TwoToOneOracle(3, "110".encode[BitRegister])
  println(Simons.run(1000)(threeQubitTwoToOneOracle))
   */

  val runSimons: NSimonsOracle => ValidatedNec[String, OneOrTwoToOne] = { sOracle =>
    QuantumRoutine.run(2000, DefaultScottyBackend)(NSimons(sOracle))(NumberQubits(6))}

  "Simons output for Identity Oracle" should "be OneToOne" in {
    assert(runSimons(NIdentityOracle) == OneToOne.validNec)
  }

  //TODO - need to measure multiple times and average, this is rather probabilistic
  "Simons output for random OneToOne Oracle" should "be OneToOne" in {
    assert(runSimons(NRandomOneToOneOracle) == OneToOne.validNec)
  }

  "Simons output for two to one b=110" should "be TwoToOne(110)" in {
    val twoToOneOracle = TwoToOneOracle(BitStringValue("110".encode[BitRegister]))
    val result = runSimons(twoToOneOracle)

  }
}
