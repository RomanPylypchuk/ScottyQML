package quantumroutines.qft

import scotty.quantum.ExperimentResult.StateStats
import utils.Measure.measureForAllInputDichotomies
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister

object QFTTest extends App{
  val testBlock1 = QFT.qftRotationBlock(3)(0)
  val testBlock2 = QFT.qftRotationBlock(3)(1)
  val testBlock3 = QFT.qftRotationBlock(3)(2)

  //println(QFT.rotationsCircuit(4))
  //println(QFT.swapCircuit(4))

  println(QFT.qftCircuit(3))

  //Test inverse
  val measure = measureForAllInputDichotomies(1000)(None)(None)
  val qftAndInverse = QFT.qftCircuit(3) combine QFT.inverseQftCircuit(3)
  val resultsQftAndInverse: Map[String, StateStats] = measure(qftAndInverse)
  assert(resultsQftAndInverse.forall{case (in, stats) => (stats.stats.length == 1) && in.reverse == stats.stats.head._1.decode[String]})

}
