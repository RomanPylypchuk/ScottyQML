package quantumroutines.qft


object ShorTest extends App {

  val fromOrder = Shor.factorFromOrder(ModularUnitaryParams(7, 15))
  val possibleFactors = fromOrder(4L)
  println(possibleFactors)
}
