package qroutines.instances.circuits

import org.scalatest.flatspec.AnyFlatSpec
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.ExperimentResult.StateStats
import utils.Measure.measureForAllInputDichotomies
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister

class QFTCircuitTest extends AnyFlatSpec{

  //TODO - add test of QFT action on basis elements: |k>

  "Applying inverse QFT after QFT circuit" should "result in identity" in {
    val nQubits = NumberQubits(4)
    val measure = measureForAllInputDichotomies(1000)(None)(None)

    val qftAndInverse = for{
      qft <- QFTCircuit.circuit
      inverse <- QFTCircuit.inverse
    } yield qft combine inverse

    val resultsQftAndInverse: Map[String, StateStats] = measure(qftAndInverse(nQubits))
    assert(resultsQftAndInverse.forall{case (in, stats) => (stats.stats.length == 1) && in.reverse == stats.stats.head._1.decode[String]})
  }
}
