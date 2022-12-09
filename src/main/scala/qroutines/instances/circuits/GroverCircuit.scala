package qroutines.instances.circuits

import cats.data.Reader
import qml.encoding.amplitude.MultipleControlled.multipleControlled
import qroutines.blocks.noracle.NOracle
import qroutines.blocks.routine.QuantumRoutineCircuit.DependentQuantumRoutineCircuit
import qroutines.instances.oracles.GroverOracle
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.Circuit
import scotty.quantum.gate.StandardGate.{H, X, Z}
import utils.GateUtils.{HTensor, XTensor}

case class GroverCircuit(usedRoutine: GroverOracle) extends DependentQuantumRoutineCircuit {
  type InParamsType = NumberQubits
  type UsedRoutineType = GroverOracle

  val inParamsToUsedRoutineParams: Reader[NumberQubits, NumberQubits] = Reader{
    nq => nq.copy(nQubits = nq.nQubits - 1)
  }

  val phaseShift: Int => Circuit = {
    nOracleQubits =>
      val nots = Circuit(XTensor(nOracleQubits): _*)
      val mcz = Circuit(multipleControlled(Z(nOracleQubits - 1), 0 until nOracleQubits - 1 :_*))
      nots combine mcz combine nots
  }

  val diffuser: Int => Circuit = {
    nOracleQubits =>
      val pShift = phaseShift(nOracleQubits)
      val hadamards: Circuit = Circuit(HTensor(nOracleQubits): _*)
      hadamards combine pShift combine hadamards
  }

  val groverIteration: Reader[NumberQubits, Circuit] = Reader{
    oracleQubits  =>
      usedRoutine.circuit(oracleQubits) combine diffuser(oracleQubits.nQubits)
  }

  val circuit: Reader[NumberQubits, Circuit] = {
    for{
      oracleQubits <- inParamsToUsedRoutineParams
    } yield{
      val NumberQubits(nOracleQubits) = oracleQubits
      val initMinusX: Circuit = Circuit.apply(Circuit.generateRegister(nOracleQubits + 1), X(nOracleQubits), H(nOracleQubits))
      val hadamards: Circuit = Circuit(HTensor(nOracleQubits): _*)
      val applyTimes: Int = math.floor((math.Pi / 4) * math.sqrt(math.pow(2, nOracleQubits) / usedRoutine.numberSolutions)).toInt
      //println("Need to apply G iteration " + applyTimes + " times.")
      val grovers: Circuit = List.fill(applyTimes)(groverIteration(oracleQubits)).reduceLeft(_ combine _)
      initMinusX combine hadamards combine grovers
    }
  }

}