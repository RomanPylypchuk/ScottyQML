package qroutines.blocks.routine

import scotty.quantum.Circuit

case class ControlledUnitaryPower(uPowerGen: (Int, Int) => Circuit)
