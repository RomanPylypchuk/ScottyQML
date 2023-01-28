package qroutines.blocks

import qroutines.blocks.modular.{ModularExponentiation, ModularUnitaryParams}
import scotty.quantum.Circuit

sealed trait CircuitParams

object CircuitParams{
  case object NoParams extends CircuitParams
  case class NumberQubits(nQubits: Int) extends CircuitParams
  case class QPEQubits(nPhaseQubits: NumberQubits, nEigenQubits: NumberQubits) extends CircuitParams
  case class QPEParams(qubits: QPEQubits, eigenStatePrep: Circuit, uPowerGen: ControlledUnitaryPower) extends CircuitParams
  case class OrderFindingParams(modParams: ModularUnitaryParams, modExp: ModularExponentiation) extends CircuitParams
}
