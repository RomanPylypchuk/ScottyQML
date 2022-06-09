package quantumroutines.qft

import cats.data.Reader
import scotty.quantum.Circuit
import scotty.quantum.gate.StandardGate.{CPHASE, H, SWAP}
import scotty.quantum.gate.{ControlGate, Gate}

object QFT {
  val cRotationK: Int => Int => ControlGate = {
      tQubit =>
        cQubit =>
        CPHASE(2 * Math.PI / Math.pow(2, cQubit - tQubit + 1), cQubit, tQubit)
  }

  val qftRotationBlock: Int => Int => List[Gate] =
    nQubits =>
      k =>
      {
        val controlledR = cRotationK(k)
        val rotations: List[ControlGate] = List.tabulate(nQubits - k - 1){i => controlledR(k+i+1)}
        H(k) :: rotations
      }

  val rotationsCircuit: Int => Circuit = {
    nQubits =>
      val qftBlock = qftRotationBlock(nQubits)
      val allQftGates = (0 until nQubits).toList.flatMap{k =>
        qftBlock(k)
      }
      Circuit(allQftGates :_*)
  }

  val swapCircuit: Int => Circuit = {
    nQubits =>
      val swaps = (0 until nQubits/2).map(i => SWAP(i, nQubits - i - 1))
      Circuit(swaps :_*)
  }

  val qftCircuit: Int => Circuit = {
    val reader = for{
      rots <- Reader(rotationsCircuit)
      swaps <- Reader(swapCircuit)
    } yield rots combine swaps
    reader.run
  }

  val inverseQftCircuit: Int => Circuit = Reader(qftCircuit).map{qftCirc =>
    val revGates = qftCirc.gates.reverse.map{
      case cRot : CPHASE => cRot.copy(phi = - cRot.phi)
      case gate => gate
    }
    Circuit(revGates :_*)
  }.run

}
