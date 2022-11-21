package quantumroutines.qft.qpe

import scotty.quantum.Circuit

sealed trait ControlledUnitaryPower{
  //Here control index, and j in U^(2^j)
  val uPowerGen: (Int, Int) => Circuit
}

object ControlledUnitaryPower{
  //case class PowerGen(uPowerGen: (Int, Int) => Circuit) extends ControlledUnitaryPower
}
