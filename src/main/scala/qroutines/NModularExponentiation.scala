package qroutines

import quantumroutines.qft.ModularUnitaryParams

trait NModularExponentiation {
  def controlPower: ModularUnitaryParams => ControlledUnitaryPower
}
