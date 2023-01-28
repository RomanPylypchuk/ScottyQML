package qroutines.blocks.noracle

import qroutines.blocks.CircuitParams.NumberQubits
import qroutines.blocks.NeedsDefinitionBy
import qroutines.blocks.routine.QuantumRoutineCircuit.IndependentQuantumRoutineCircuit

trait NOracle extends IndependentQuantumRoutineCircuit with NeedsDefinitionBy[OracleDefinitions]{
  type InParamsType = NumberQubits
}
