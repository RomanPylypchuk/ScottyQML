package qroutines.instances.circuits.elementary

import cats.data.Reader
import qroutines.instances.oracles.NSimonsOracle
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.Circuit
import utils.GateUtils.HTensor

case class NSimonsCircuit(usedRoutine: NSimonsOracle) extends NElementaryCircuit{

  type UsedRoutineType = NSimonsOracle

  val inParamsToUsedRoutineParams: Reader[NumberQubits, NumberQubits] = Reader{
    nq => nq.copy(nQubits = nq.nQubits / 2)
  }

  val preOracle: Reader[NumberQubits, Circuit] =
    Reader {
      case NumberQubits(nQubits) =>
        val init: Circuit = Circuit(Circuit.generateRegister(nQubits))
        init combine Circuit(HTensor(nQubits / 2): _*)
    }

     val postOracle: Reader[NumberQubits, Circuit] = Reader{
       case NumberQubits(nQubits) =>
         Circuit(HTensor(nQubits / 2): _*)
     }

}
