package quantumroutines

package object deutschjosza {

  sealed trait ConstantOrBalanced
  final case object Constant extends ConstantOrBalanced
  final case object Balanced extends ConstantOrBalanced

  /*
  val testCircuit = Circuit(QubitRegister("00"), H(0))
  val qs = QuantumSimulator()
  val z = (0 to 100).map(_ => qs.run(testCircuit) match {
    case sp: Superposition =>
      //println(sp.state.toList)
      qs.measure(QubitRegister("0"), sp.state) //This doesn't actually measure only 1st qubit!
    case c: Collapsed => c
  }).toList

  val res = ExperimentResult(z)
  res.trials.foreach(println)
}*/

}