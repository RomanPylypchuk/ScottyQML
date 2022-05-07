

object ExtraGatesTest extends App{
  ///////////////////////////////////////

  /*val ec = ExecutionContext.fromExecutor(
    new java.util.concurrent.ForkJoinPool(8)
  )

  implicit val qec = QuantumSimulator(ec)

  val register = QubitRegister(Qubit.zero("my_qubit_1"), Qubit.zero("my_qubit_2"))
  val circuit = Circuit(X(0), CNOT(0, 1), X(0)).withRegister(register)
  val sp = QuantumSimulator().run(circuit)

  //Must be |01> only
  println(StateProbabilityReader(sp).toHumanString)
  */
}
