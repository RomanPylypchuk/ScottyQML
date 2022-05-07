package quantumroutines.deutschjosza

import scotty.quantum.gate.StandardGate.{H, X}
import scotty.quantum.{Circuit, Collapsed, QubitRegister, Superposition}
import scotty.simulator.QuantumSimulator
import utils.{HTensor, paddedIntToBinary}

object DeutschJosza {

  def deutschJosza(function: Oracle): Circuit = {
    val nQubits = function.nOracleQubits + 1
    val left: Circuit = Circuit.apply(Circuit.generateRegister(nQubits), X(nQubits - 1), H(nQubits - 1))
    val oracleHadamards: Circuit = Circuit(HTensor(nQubits - 1) :_*)

    left combine oracleHadamards combine function.oracle combine oracleHadamards
  }

  def runDeutschJosza(function: Oracle): ConstantOrBalanced = {
    val dj: Circuit = deutschJosza(function)

    val qs = QuantumSimulator()

    val trials = (0 until 1000).map(_ => qs.run(dj) match {
      case sp: Superposition =>
        qs.measure(QubitRegister("0"), sp.state)
      case c: Collapsed => c
    })

    val cutNQubits = dj.register.size - 1

    //This is not very nice, but can suffice for now, because proper
    //measurement on a subset of qubits is not implemented in Scotty :(
    val existsNonZeroOutcome = trials.exists(collapsed =>
      !paddedIntToBinary(cutNQubits + 1)(collapsed.index).takeRight(cutNQubits).forall(_ == '0')
    )

    if (existsNonZeroOutcome) Balanced else Constant
  }

}
