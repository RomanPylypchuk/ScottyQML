package utils

import scotty.quantum.gate.StandardGate.{CNOT, H}
import scotty.quantum.{BitRegister, Circuit}
import utils.Measure.{StateStatsOps, embedDichotomyBits, measureTimes}
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister


object MeasureTest extends App{
  val EPRCircuit: Circuit = Circuit(H(0), CNOT(0,1))
  println(measureTimes(1000)(EPRCircuit).forQubits(Set(1)))

  println(embedDichotomyBits(10)(Set(1, 4, 6))("111".encode[BitRegister]))

}
