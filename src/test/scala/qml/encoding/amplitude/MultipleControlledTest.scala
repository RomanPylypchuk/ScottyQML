package qml.encoding.amplitude

import qml.encoding.amplitude.MultipleControlled.{controlledConfigurationGate, inverseControlled, multipleControlled}
import scotty.quantum.gate.StandardGate.{CCNOT, X}
import scotty.quantum.{Circuit, One, Zero}
import scotty.simulator.QuantumSimulator

object MultipleControlledTest extends App{

  /////////////////////////////////////////////////
  val toffoliAsMultiple = multipleControlled(X(2), 0, 1)
  val results1 = QuantumSimulator()
    .runExperiment(Circuit(X(0), X(1), toffoliAsMultiple), 1000)
    .stateStats.toHumanString

  println(results1)
  /////////////////////////////////////////////////
  val invCCNOT = inverseControlled(CCNOT(0, 1, 2))
  val results2 = QuantumSimulator()
    .runExperiment(Circuit(invCCNOT), 1000)
    .stateStats.toHumanString

  println(results2)
  /////////////////////////////////////////////////
  val controlled = controlledConfigurationGate(Map(0 -> Zero(), 1 -> One(), 2 -> One()))(X(3))
  val results3 = QuantumSimulator()
    .runExperiment(Circuit(X(1), X(2), controlled), 1000)
    .stateStats.toHumanString

  println(results3)
  /////////////////////////////////////////////////
}
