package utils

import scotty.quantum.ExperimentResult.StateStats
import scotty.quantum._
import scotty.simulator.QuantumSimulator
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.{BitRegisterTo, stringBitRegister}

object Measure {

  val projectDichotomyBits: Set[Int] => BitRegister => BitRegister = {
    qubits =>
      bitRegister =>
        val nQubits = bitRegister.size
        val reducedBitValues = bitRegister.values.zipWithIndex.collect { case (bit, idx) if qubits(nQubits - 1 - idx) => bit }
        BitRegister(reducedBitValues: _*)
  }

  val embedDichotomyBits: Int => Set[Int] => BitRegister => BitRegister =
    eQubits => {
      val emptyE: List[Bit] = List.fill(eQubits)(Zero())
      qubits => {
        val sQubits: List[Int] = qubits.toList.sorted
        bitRegister => {
          val updates: List[(Int, Bit)] = sQubits.zip(bitRegister.values)
          val embedded = updates.foldLeft(emptyE) {
            case (eb, (idx, One(_))) => eb.updated(idx, One())
            case (eb, _) => eb
          }
          BitRegister(embedded: _*)
        }
      }
    }

  implicit class StateStatsOps(val measurements: StateStats) {

    private def nQubits: Int = measurements.stats.head._1.size

    def transform: (List[(BitRegister, Int)] => List[(BitRegister, Int)]) => StateStats =
      {tFunction =>
          StateStats(tFunction(measurements.stats))
      }

    //TODO - via transform
    def reverseQubitOrder: StateStats =
      measurements.copy(stats =
        measurements.stats.map{case (dichotomy, times) => dichotomy.reverse -> times}
        )

    def forQubits(qubits: Set[Int]): StateStats = {
      val stats = measurements.stats
      val reducedDichotomies = stats.map { case (fullDichotomy, times) => projectDichotomyBits(qubits)(fullDichotomy) -> times }
      val reducedStats: Map[BitRegister, Int] =
        reducedDichotomies.groupMapReduce { case (dichotomy, _) => dichotomy } { case (_, times) => times }(_ + _)
      StateStats(reducedStats.toList)
    }

    def exceptQubits(qubits: Set[Int]): StateStats = forQubits((0 until nQubits).toSet diff qubits)
  }

  val measureTimes: Int => Circuit => StateStats = {
    trials =>
      circuit => {

        val results = QuantumSimulator()
          .runExperiment(circuit, trialsCount = trials)
          .stateStats
        results
      }
  }


  //TODO - parallelize measuring for each dichotomy, e.g. via Future
  val measureForAllInputDichotomies:
    Int => Option[Set[Int]] => Option[Set[Int]] =>
      Circuit => Map[String, StateStats] = {
    trials =>
      maybeDQubits =>
        maybeMQubits =>
          circuit => {
            val nQubits = circuit.register.size
            val measure = measureTimes(trials)
            val embedDichotomy: BitRegister => BitRegister =
              maybeDQubits.fold(identity[BitRegister] _)(dQubits => embedDichotomyBits(nQubits)(dQubits))

            val inputBasis: List[String] = allDichotomies(maybeDQubits.fold(nQubits)(_.size))
            val prepareStates: List[Circuit] = inputBasis.map(dichotomy => embedDichotomy(dichotomy.encode[BitRegister]).toCircuit)

            val rawMeasurements = inputBasis.zip(prepareStates).map {
              case (dichotomy, pCircuit) =>
                dichotomy -> filterStats(measure(pCircuit combine circuit))
            }.toMap

            maybeMQubits.fold(rawMeasurements)(mQubits =>
              rawMeasurements.map { case (dichotomy, fullStats) => dichotomy -> fullStats.forQubits(mQubits) })
          }
  }

  //val filterStats: StateStats => String = results => results.copy(stats = results.stats.filter { case (_, i) => i != 0 }).toHumanString
  val filterStats: StateStats => StateStats = results => results.copy(stats = results.stats.filter { case (_, i) => i != 0 })

}
