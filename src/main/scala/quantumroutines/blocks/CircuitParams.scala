package quantumroutines.blocks

import qroutines.{ControlledUnitaryPower, NModularExponentiation}
import quantumroutines.qft.ModularUnitaryParams
import scotty.quantum.Circuit

sealed trait CircuitParams

object CircuitParams{
  case object NoParams extends CircuitParams
  case class NumberQubits(nQubits: Int) extends CircuitParams
  case class QPEQubits(nPhaseQubits: NumberQubits, nEigenQubits: NumberQubits) extends CircuitParams
  case class QPEParams(qubits: QPEQubits, eigenStatePrep: Circuit, uPowerGen: ControlledUnitaryPower) extends CircuitParams
  case class OrderFindingParams(modParams: ModularUnitaryParams, modExp: NModularExponentiation) extends CircuitParams


  case class QPEQubitsOld(nPhaseQubits: Int, nEigenQubits: Int) extends CircuitParams
}
