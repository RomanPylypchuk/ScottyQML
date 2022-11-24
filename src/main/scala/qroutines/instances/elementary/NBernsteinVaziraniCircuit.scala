package qroutines.instances.elementary

import qroutines.noracle.instances.NInnerProductOracle

case class NBernsteinVaziraniCircuit(usedRoutine: NInnerProductOracle) extends NDeutschJoszaLikeCircuit{

  type UsedRoutineType = NInnerProductOracle
}
