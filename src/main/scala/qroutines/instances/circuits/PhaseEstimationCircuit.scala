package qroutines.instances.circuits

import cats.data.Reader
import qroutines.blocks.routine.QuantumRoutineCircuit.DependentQuantumRoutineCircuit
import quantumroutines.blocks.CircuitParams.{NumberQubits, QPEParams, QPEQubits}
import quantumroutines.blocks.CircuitWithParams
import scotty.quantum.Circuit
import utils.GateUtils.HTensor

object PhaseEstimationCircuit extends DependentQuantumRoutineCircuit {
  type InParamsType = QPEParams
  type UsedRoutineType = QFTCircuit.type
  type OutParamsType = QPEParams

  val usedRoutine: QFTCircuit.type = QFTCircuit
  val inParamsToUsedRoutineParams: Reader[QPEParams, NumberQubits] = Reader(_.qubits.nPhaseQubits)

  val initCircuit: Int => Int => Circuit =
    nPhaseQubits =>
      nEigenQubits => {
        Circuit.apply(Circuit.generateRegister(nPhaseQubits + nEigenQubits))
      }

  val controlBlock: Int => ((Int, Int) => Circuit) => Circuit =
    nPhaseQubits =>
      cUPower2Gen => {
        val controlledUs = (0 until nPhaseQubits).map { cIdx =>
          cUPower2Gen(cIdx, nPhaseQubits - cIdx - 1)
        }
        controlledUs.reduceLeft(_ combine _)
      }

  val circuit: Reader[QPEParams, Circuit] = {

    val preQft: Reader[QPEParams, Circuit] = Reader {
      case QPEParams(
      QPEQubits(NumberQubits(nPhaseQubits), NumberQubits(nEigenQubits)), eigenStatePrep, cUnitaryPower) =>

        val init = initCircuit(nPhaseQubits)
        val hadamards = Circuit(HTensor(nPhaseQubits): _*)
        val cBlock = controlBlock(nPhaseQubits)

        init(nEigenQubits) combine eigenStatePrep combine hadamards combine cBlock(cUnitaryPower.uPowerGen)
    }

    for {
      param <- Reader[QPEParams, QPEParams](identity)
      preQftCirc <- preQft
      qftParams <- inParamsToUsedRoutineParams
    } yield {
      val qpe: Circuit = preQftCirc combine usedRoutine.inverse(qftParams)
      qpe
    }
  }

}
