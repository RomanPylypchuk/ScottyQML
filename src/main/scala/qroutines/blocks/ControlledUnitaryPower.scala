package qroutines.blocks

import scotty.quantum.Circuit

case class ControlledUnitaryPower(uPowerGen: (Int, Int) => Circuit)
