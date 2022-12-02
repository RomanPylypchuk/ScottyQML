package qroutines.instances.routines

import qroutines.blocks.routine.{QuantumRoutine, QuantumRoutineCircuit}
import qroutines.instances.circuits.elementary.NDeutschJoszaCircuit
import quantumroutines.blocks.CircuitParams.NumberQubits

trait NDeutschJosza extends QuantumRoutine{
  type InParamsType = NumberQubits

}
