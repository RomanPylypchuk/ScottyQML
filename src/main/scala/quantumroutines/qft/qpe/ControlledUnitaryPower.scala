package quantumroutines.qft.qpe

import scotty.quantum.Circuit

trait ControlledUnitaryPower{
  //Here control index, and j in U^(2^j)
  val uPowerGen: (Int, Int) => Circuit
}

object ControlledUnitaryPower{
  //Apply, etc
}
