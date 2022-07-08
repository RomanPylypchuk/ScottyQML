package quantumroutines.qft

import scotty.quantum.BitRegister
import scotty.quantum.ExperimentResult.StateStats
import utils.Measure.StateStatsOps
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.decimalBitRegister

object OrderFindingTest extends App{

  //StatsConvergent Test
  val measurementOutcome1 = StateStats(List(
     1536.encodeE[Int, BitRegister](11) -> 600,
   )).reverseQubitOrder

  val estimate1 = OrderFinding.statsConvergent(measurementOutcome1)
  println(estimate1)

  val measurementOutcome2 = StateStats(List(
    512.encodeE[Int, BitRegister](11) -> 400
  )).reverseQubitOrder

  val estimate2 = OrderFinding.statsConvergent(measurementOutcome2)
  println(estimate2)

  val combineTwoEstimates = OrderFinding.order(7)(15)
  println(combineTwoEstimates(estimate1, estimate2))

  //TODO - Test for some real outputs of quantum algorithm
}
