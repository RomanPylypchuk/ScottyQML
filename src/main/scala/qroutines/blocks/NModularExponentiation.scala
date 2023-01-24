package qroutines.blocks

import quantumroutines.qft.ModularUnitaryParams
import scotty.quantum.Circuit

trait NModularExponentiation {
  def controlPower: ModularUnitaryParams => ControlledUnitaryPower
}

object NModularExponentiation{
  object DummyExponentiation extends NModularExponentiation{
    def controlPower: ModularUnitaryParams => ControlledUnitaryPower = _ => ControlledUnitaryPower((_, _) => Circuit())
  }
}
