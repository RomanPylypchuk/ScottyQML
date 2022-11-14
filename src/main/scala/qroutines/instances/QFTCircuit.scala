package qroutines.instances

import cats.data.Reader
import qroutines.QuantumRoutineCircuit.IndependentQuantumRoutineCircuit
import quantumroutines.blocks.CircuitParams.NumberQubits
import quantumroutines.blocks.CircuitWithParams
import scotty.quantum.Circuit
import scotty.quantum.gate.StandardGate.{CPHASE, H, SWAP}
import scotty.quantum.gate.{ControlGate, Gate}

object QFTCircuit extends IndependentQuantumRoutineCircuit {

  type InParamsType = NumberQubits
  type OutParamsType = NumberQubits

  val cRotationK: Int => Int => ControlGate = {
    tQubit =>
      cQubit =>
        CPHASE(2 * Math.PI / Math.pow(2, cQubit - tQubit + 1), cQubit, tQubit)
  }

  val qftRotationBlock: Int => Int => List[Gate] =
    nQubits =>
      k => {
        val controlledR = cRotationK(k)
        val rotations: List[ControlGate] = List.tabulate(nQubits - k - 1) { i => controlledR(k + i + 1) }
        H(k) :: rotations
      }

  val rotationsCircuit: Int => Circuit = {
    nQubits =>
      val qftBlock = qftRotationBlock(nQubits)
      val allQftGates = (0 until nQubits).toList.flatMap { k =>
        qftBlock(k)
      }
      Circuit(allQftGates: _*)
  }

  val swapCircuit: Int => Circuit = {
    nQubits =>
      val swaps = (0 until nQubits / 2).map(i => SWAP(i, nQubits - i - 1))
      Circuit(swaps: _*)
  }

  val circuit: Reader[NumberQubits, CircuitWithParams[NumberQubits]] = {

    val qft: Reader[Int, CircuitWithParams[NumberQubits]] = for {
      qubits <- Reader[Int, NumberQubits](NumberQubits)
      rots <- Reader(rotationsCircuit)
      swaps <- Reader(swapCircuit)
    } yield CircuitWithParams(rots combine swaps, qubits)

    val project: Reader[NumberQubits, Int] = Reader[NumberQubits, Int](_.nQubits)

    qft compose project
  }

}
