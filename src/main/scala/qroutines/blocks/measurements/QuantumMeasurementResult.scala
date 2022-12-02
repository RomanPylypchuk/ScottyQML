package qroutines.blocks.measurements

import scotty.quantum.ExperimentResult.StateStats

case class QuantumMeasurementResult(stats: StateStats, params: QuantumMeasurementParams)
