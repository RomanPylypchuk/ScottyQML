package qroutines.instances.elementary

import qroutines.noracle.instances.NDeutschJoszaOracle

case class NDeutschJoszaCircuit(usedRoutine: NDeutschJoszaOracle) extends NDeutschJoszaLikeCircuit {

  type UsedRoutineType = NDeutschJoszaOracle

}
