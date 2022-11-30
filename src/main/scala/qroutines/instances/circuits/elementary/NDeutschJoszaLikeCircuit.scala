package qroutines.instances.circuits.elementary

import cats.data.Reader
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.Circuit
import scotty.quantum.gate.StandardGate.{H, X}
import utils.GateUtils.HTensor

trait NDeutschJoszaLikeCircuit extends NElementaryCircuit {

  val inParamsToUsedRoutineParams: Reader[NumberQubits, NumberQubits] = Reader{
    nq => nq.copy(nQubits = nq.nQubits - 1)
  }

  val preOracle: Reader[NumberQubits, Circuit] = {
    Reader{
      case NumberQubits(nQubits) =>
        val initMinusX: Circuit = Circuit.apply(Circuit.generateRegister(nQubits), X(nQubits - 1), H(nQubits - 1))
        initMinusX combine Circuit(HTensor(nQubits - 1): _*)
    }
  }

  val postOracle: Reader[NumberQubits, Circuit] = {
    Reader{
      case NumberQubits(nQubits) => Circuit(HTensor(nQubits - 1): _*)
    }
  }

}
