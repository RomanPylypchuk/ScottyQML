import encoding.amplitude.MultipleControlled.controlledConfigurationGate
import scotty.quantum.gate.StandardGate.{H, X}
import scotty.quantum.{Circuit, One, Zero}
import utils.Measure.measureForAllInputDichotomies

object NielsenChuangTest extends App{

  //Implement two-level unitary for example unitary 4.58, where a,b,c,d correspond to H
  val greyOne = controlledConfigurationGate(Map(0 -> Zero(), 1 -> Zero()))(X(2))
  val greyTwo = controlledConfigurationGate(Map(0 -> Zero(), 2 -> One()))(X(1))
  val grey = Circuit(greyOne, greyTwo) //TODO - Need implementation of Grey code for any two-level unitary
  val greyReverse = Circuit(greyTwo, greyOne)

  val controlledUnitary = controlledConfigurationGate(Map(1 -> One(), 2 -> One()))(H(0))
  val twoLevelUnitary: Circuit = grey combine controlledUnitary combine greyReverse
  //

  val measurements = measureForAllInputDichotomies(1000)(twoLevelUnitary)
  println(measurements)


}
