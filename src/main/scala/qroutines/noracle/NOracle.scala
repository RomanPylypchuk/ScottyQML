package qroutines.noracle

import qroutines.NeedsDefinitionBy
import qroutines.QuantumRoutineCircuit.IndependentQuantumRoutineCircuit
import quantumroutines.blocks.CircuitParams.NumberQubits

trait NOracle extends IndependentQuantumRoutineCircuit with NeedsDefinitionBy[OracleDefinitions]{
  type InParamsType = NumberQubits
  type OutParamsType = NumberQubits
}
