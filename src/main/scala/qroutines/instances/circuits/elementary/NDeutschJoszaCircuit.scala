package qroutines.instances.circuits.elementary

import qroutines.instances.oracles.NDeutschJoszaOracle

case class NDeutschJoszaCircuit(usedRoutine: NDeutschJoszaOracle) extends NDeutschJoszaLikeCircuit {

  type UsedRoutineType = NDeutschJoszaOracle

}
