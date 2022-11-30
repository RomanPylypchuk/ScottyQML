package qroutines.blocks.measurements

import scotty.quantum.ExperimentResult.StateStats

case class MeasurementResult(stats: StateStats, params: MeasurementParams)
