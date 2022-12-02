package qroutines.blocks.noracle

import qroutines.NeedsDefinitionBy
import qroutines.blocks.routine.QuantumRoutineCircuit.IndependentQuantumRoutineCircuit
import quantumroutines.blocks.CircuitParams.NumberQubits

trait NOracle extends IndependentQuantumRoutineCircuit with NeedsDefinitionBy[OracleDefinitions]{
  type InParamsType = NumberQubits
}
