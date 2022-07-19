package quantumroutines.qft

import cats.implicits.catsSyntaxValidatedIdBinCompat0


object ShorTest extends App {

  val fromOrder = Shor.factorFromOrder(ModularUnitaryParams(7, 15))
  val possibleFactors = fromOrder(4L.validNec)
  println(possibleFactors)
}
