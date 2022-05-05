package quantumroutines

import quantumroutines.TwoLevelUnitary.twoLevelUnitary
import scotty.quantum.gate.StandardGate.H
import utils.Measure.measureForAllInputDichotomies

object TwoLevelUnitaryTest extends App{

  /*
  //Implement two-level unitary for example unitary 4.58, where a,b,c,d correspond to H
  val greyOne = controlledConfigurationGate(Map(0 -> Zero(), 1 -> Zero()))(X(2))
  val greyTwo = controlledConfigurationGate(Map(0 -> Zero(), 2 -> One()))(X(1))
  val grey = Circuit(greyOne, greyTwo)
  val greyReverse = Circuit(greyTwo, greyOne)

  val controlledUnitary = controlledConfigurationGate(Map(1 -> One(), 2 -> One()))(H(0))
  val twoLevelUnitary: Circuit = grey combine controlledUnitary combine greyReverse
  //
   */

  val twoLevelU = twoLevelUnitary(3)(0, 7)(H(0).matrix)
  val measurements = measureForAllInputDichotomies(1000)(twoLevelU)
  println(measurements)


}
