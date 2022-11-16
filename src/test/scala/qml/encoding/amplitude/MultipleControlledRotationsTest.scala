package qml.encoding.amplitude

import qml.encoding.amplitude.MultiControlledRotations.{RYAngles, amplitudeEncodeCascade}
import scotty.quantum.{Circuit, QubitRegister, StateProbabilityReader, Superposition}
import scotty.simulator.QuantumSimulator
import utils.factory.SuperpositionFactory

import scala.concurrent.ExecutionContext

object MultipleControlledRotationsTest extends App {

  //This is state from Example 5.1
  val exampleState: Superposition = SuperpositionFactory(
    Map("000" -> math.sqrt(0.2), "010" -> math.sqrt(0.5), "110" -> math.sqrt(0.2), "111" -> math.sqrt(0.1))
  ).get

  val beta = RYAngles(exampleState)
  val nQubits = 3
  val rotationsAngles = for{
     s <- 1 to nQubits
     j <- 1 to math.pow(2, nQubits - s).toInt
   } yield (s, j, beta(s)(j))

  println(rotationsAngles)


  val allBlocks = amplitudeEncodeCascade(exampleState)
  println(allBlocks)

  val ec = ExecutionContext.fromExecutor(
    new java.util.concurrent.ForkJoinPool(8)
  )

implicit val qec: QuantumSimulator = QuantumSimulator(ec)

  val register = QubitRegister("000")
  val circuit = Circuit(allBlocks :_*).withRegister(register)
  val sp = QuantumSimulator().run(circuit)

  //Must be |01> only
  println(StateProbabilityReader(sp).toHumanString)

}
