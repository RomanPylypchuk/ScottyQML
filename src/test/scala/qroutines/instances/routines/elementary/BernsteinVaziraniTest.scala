package qroutines.instances.routines.elementary

import cats.data.ValidatedNec
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import org.scalatest.flatspec.AnyFlatSpec
import qroutines.blocks.CircuitParams.NumberQubits
import qroutines.blocks.measurements.QuantumMeasurementBackend.DefaultScottyBackend
import qroutines.blocks.noracle.OracleDefinitions.BitStringValue
import qroutines.blocks.routine.QuantumRoutine
import qroutines.blocks.routine.QuantumRoutineOutput.BitStringOutput
import qroutines.instances.oracles.InnerProductOracle
import scotty.quantum.BitRegister
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister

class BernsteinVaziraniTest extends AnyFlatSpec{

  val runBernsteinVazirani: InnerProductOracle => ValidatedNec[String, BitStringOutput] = { bvOracle =>
    QuantumRoutine.run(1000, DefaultScottyBackend)(BernsteinVazirani(bvOracle))(NumberQubits(5))}

  "Bernstein Vazirani output for b=1011" should "be 1011.reverse" in {
    val oracleBinary = "1011".encode[BitRegister]
    val iOracle = InnerProductOracle(BitStringValue(oracleBinary))
    assert(runBernsteinVazirani(iOracle) == BitStringOutput("1101".encode[BitRegister]).validNec)
  }
}
