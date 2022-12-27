package qroutines.blocks.measurements

import cats.data.Reader
import scotty.quantum.Circuit
import scotty.quantum.ExperimentResult.StateStats
import utils.Measure.{StateStatsOps, measureTimes}

trait QuantumMeasurementBackend {
  val measure: QuantumMeasurementParams => Reader[Circuit, QuantumMeasurementResult]
}

object QuantumMeasurementBackend {

  object DefaultScottyBackend extends QuantumMeasurementBackend {

    val measure: QuantumMeasurementParams => Reader[Circuit, QuantumMeasurementResult] = {
      case qmParams@QuantumMeasurementParams(times, measureQubits) =>

        Reader { circuit =>
          val measuredStats: StateStats = measureTimes(times)(circuit).forQubits(measureQubits)
          QuantumMeasurementResult(measuredStats, qmParams)
        }
    }
  }
}