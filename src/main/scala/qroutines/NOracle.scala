package qroutines

import qroutines.QuantumRoutineCircuit.IndependentQuantumRoutineCircuit
import quantumroutines.blocks.CircuitParams.NumberQubits

//TODO - Perhaps add here extra type member for creation of Oracle, e.g. Bit or BitRegister
trait NOracle extends IndependentQuantumRoutineCircuit{
  type InParamsType = NumberQubits
  type OutParamsType = NumberQubits
}
