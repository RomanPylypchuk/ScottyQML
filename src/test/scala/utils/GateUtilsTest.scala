package utils

import scotty.quantum.gate.StandardGate._
import scotty.quantum.{BitRegister, Circuit}
import utils.GateUtils.{InverseCircuit, inverseGate}
import utils.Measure.measureTimes
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister

object GateUtilsTest extends App{

  //Inverse of TargetGate
  val xAtZero = X(0)
  val xInverse = inverseGate(xAtZero)

  val xStats = measureTimes(1000)(Circuit(xAtZero, xInverse))
  assert(xStats.stats.toMap.get("0".encode[BitRegister]).contains(1000))

  //Inverse of SwapGate
  val swapZeroOne = SWAP(0,1)
  val inverseSwapZeroOne = inverseGate(swapZeroOne)
  val swapStats = measureTimes(1000)(Circuit(X(0), swapZeroOne, inverseSwapZeroOne))
  assert(swapStats.stats.toMap.get("01".encode[BitRegister]).contains(1000))

  //Inverse of ControlGate

  //CNOT
  val cNotZeroOne = CNOT(0,1)
  val inverseCNOTZeroOne = inverseGate(cNotZeroOne)
  val cNOTStats = measureTimes(1000)(Circuit(X(0), cNotZeroOne, inverseCNOTZeroOne))
  assert(cNOTStats.stats.toMap.get("01".encode[BitRegister]).contains(1000))

  //CSWAP(Fredkin)
  val fredkinZeroOneTwo = CSWAP(0,1,2)
  val inverseFredkin = inverseGate(fredkinZeroOneTwo)
  val cSwapStats = measureTimes(1000)(Circuit(X(0), fredkinZeroOneTwo))
  assert(cSwapStats.stats.toMap.get("001".encode[BitRegister]).contains(1000))

  //Inverse Circuit test
  val qc = Circuit(X(0), Z(1), CNOT(0,1), SWAP(0,1), CNOT(1,0), Z(0))
  val inverseQc = qc.dagger

  println(measureTimes(1000)(qc combine inverseQc))
}
