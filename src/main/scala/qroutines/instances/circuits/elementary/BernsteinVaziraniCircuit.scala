package qroutines.instances.circuits.elementary

import qroutines.instances.oracles.InnerProductOracle

case class BernsteinVaziraniCircuit(usedRoutine: InnerProductOracle) extends DeutschJoszaLikeCircuit{

  type UsedRoutineType = InnerProductOracle
}
