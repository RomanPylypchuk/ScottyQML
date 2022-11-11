package quantumroutines.blocks

import quantumroutines.qft.qpe.{ControlledUnitaryPower, QPEQubits}
import quantumroutines.qft.{ModularExponentiation, ModularUnitaryParams}
import scotty.quantum.Circuit

sealed trait CircuitParams

object CircuitParams{
  case object NoParams extends CircuitParams
  case class NumberQubits(nQubits: Int) extends CircuitParams
  case class QPEParams(qubits: QPEQubits, eigenStatePrep: Circuit, uPowerGen: ControlledUnitaryPower) extends CircuitParams
  case class OrderFindingParams(modParams: ModularUnitaryParams, modExp: ModularExponentiation) extends CircuitParams


  case class QPEQubitsOld(nPhaseQubits: Int, nEigenQubits: Int) extends CircuitParams
}
