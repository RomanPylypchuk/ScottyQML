package qroutines.blocks.modular

import qroutines.blocks.ControlledUnitaryPower
import scotty.quantum.Circuit

trait ModularExponentiation {
  def controlPower: ModularUnitaryParams => ControlledUnitaryPower
}

object ModularExponentiation{
  object DummyExponentiation extends ModularExponentiation{
    def controlPower: ModularUnitaryParams => ControlledUnitaryPower = _ => ControlledUnitaryPower((_, _) => Circuit())
  }
}
