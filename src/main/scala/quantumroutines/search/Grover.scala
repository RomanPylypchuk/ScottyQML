package quantumroutines.search

import qml.encoding.amplitude.MultipleControlled.multipleControlled
import quantumroutines.oracle.Oracle
import scotty.quantum.Circuit
import scotty.quantum.gate.StandardGate.{H, X, Z}
import utils.GateUtils.{HTensor, XTensor}

object Grover {
  //This has many similarities with stuff in ElementaryCircuit - initialization, Oracle, etc.

  val phaseShift: Int => Circuit = {
    nOracleQubits =>
      val nots = Circuit(XTensor(nOracleQubits): _*)
      val mcz = Circuit(multipleControlled(Z(nOracleQubits - 1), 0 until nOracleQubits - 1 :_*))
      nots combine mcz combine nots
    //val controlAllOnes: BitRegister = ("1" * (nOracleQubits - 1)).encode[BitRegister]
    //val cGate = controlledConfigurationGate(dichotomyToControlMap(controlAllOnes))(Z(nOracleQubits - 1))
    //Circuit(cGate)
  }

  val diffuser: Int => Circuit = {
    nOracleQubits =>
      val pShift = phaseShift(nOracleQubits)
      val hadamards: Circuit = Circuit(HTensor(nOracleQubits): _*)
      hadamards combine pShift combine hadamards
  }

  val groverIteration: Oracle => Circuit = {
    gOracle =>
      gOracle.oracle combine diffuser(gOracle.nOracleQubits)
  }

  //TODO - perhaps factor out number of solutions m someplace else
  val grover: Int => Oracle => Circuit = {
    m =>
      gOracle =>
        val initMinusX: Circuit = Circuit.apply(Circuit.generateRegister(gOracle.nOracleQubits + 1), X(gOracle.nOracleQubits), H(gOracle.nOracleQubits)) //Same as DJ
        val hadamards: Circuit = Circuit(HTensor(gOracle.nOracleQubits): _*)
        val applyTimes = math.floor((math.Pi / 4) * math.sqrt(math.pow(2, gOracle.nOracleQubits) / m)).toInt
        println("Need to apply G iteration " + applyTimes + " times.")
        val grovers: Circuit = List.fill(applyTimes)(groverIteration(gOracle)).reduceLeft(_ combine _)
        initMinusX combine hadamards combine grovers
  }
}
