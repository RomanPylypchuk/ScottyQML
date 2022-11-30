package qroutines.instances.circuits.elementary

import qroutines.instances.oracles.NInnerProductOracle

case class NBernsteinVaziraniCircuit(usedRoutine: NInnerProductOracle) extends NDeutschJoszaLikeCircuit{

  type UsedRoutineType = NInnerProductOracle
}
