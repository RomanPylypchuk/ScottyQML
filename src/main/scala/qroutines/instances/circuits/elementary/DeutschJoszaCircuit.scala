package qroutines.instances.circuits.elementary

import qroutines.instances.oracles.DeutschJoszaOracle

case class DeutschJoszaCircuit(usedRoutine: DeutschJoszaOracle) extends DeutschJoszaLikeCircuit {

  type UsedRoutineType = DeutschJoszaOracle

}
