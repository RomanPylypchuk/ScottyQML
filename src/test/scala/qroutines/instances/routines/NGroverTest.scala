package qroutines.instances.routines

import cats.data.Reader
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import org.scalatest.flatspec.AnyFlatSpec
import qml.encoding.amplitude.MultipleControlled.controlledConfigurationGate
import qroutines.blocks.measurements.QuantumMeasurementBackend.DefaultScottyBackend
import qroutines.blocks.noracle.OracleDefinitions.{BitStringValue, BitStrings}
import qroutines.blocks.routine.QuantumRoutine
import qroutines.blocks.routine.QuantumRoutineOutput.BitStringsOutput
import qroutines.instances.oracles.GroverOracle
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.gate.StandardGate.{CCNOT, X}
import scotty.quantum.{Bit, BitRegister, Circuit}
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.{BitRegisterOps, controlMapBitRegister, stringBitRegister}

class NGroverTest extends AnyFlatSpec{

  "Grover output for Oracle w = |11>" should "11" in {
    val w = "11".encode[BitRegister]
    val test2Oracle = new GroverOracle{
      type DefiningType = BitStringValue
      val numberSolutions: Int = 1
      val definingObject: BitStringValue = BitStringValue(w)
      val circuit: Reader[NumberQubits, Circuit] = Reader(_ => Circuit(CCNOT(0,1,2))) //only f(1,1) = 1
    }

    val result = QuantumRoutine.run(1000, DefaultScottyBackend)(NGrover(test2Oracle))(NumberQubits(3))
    assert(result == BitStringsOutput(List(w)).validNec)
  }

  "Grover output for Oracle(ws=101,110)" should "contain 101,110" in {

    val ws = List("101", "110").map(_.encode[BitRegister])
    val test3Oracle = new GroverOracle{
      type DefiningType = BitStrings
      val numberSolutions: Int = 2
      val definingObject: BitStrings = BitStrings(ws)
      val circuit: Reader[NumberQubits, Circuit] = Reader { _ =>
        val controlMaps = ws.map(_.decodeE[Int, Map[Int, Bit]](3))
        val controls = controlMaps.map(wMap => controlledConfigurationGate(wMap)(X(3)))
        Circuit(controls :_*)
      }
    }

    val result = QuantumRoutine.run(1000, DefaultScottyBackend)(NGrover(test3Oracle))(NumberQubits(4))
    assert(result == BitStringsOutput(ws.map(_.reverse)).validNec)
  }

}
