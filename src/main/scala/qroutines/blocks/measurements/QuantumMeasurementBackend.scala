package qroutines.blocks.measurements

import cats.data.Reader
import scotty.quantum.Circuit
import scotty.quantum.ExperimentResult.StateStats

trait QuantumMeasurementBackend {
  val measure: QuantumMeasurementParams => Reader[Circuit, QuantumMeasurementResult]
}

object QuantumMeasurementBackend{
  object DummyBackend extends QuantumMeasurementBackend{
    val measure: QuantumMeasurementParams => Reader[Circuit, QuantumMeasurementResult] =
      _ => Reader[Circuit, QuantumMeasurementResult]{_ => null}
  }
}