package quantumroutines.qft

import cats.data.{Reader, ValidatedNec}
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import quantumroutines.qft.OrderFinding.order
import utils.algebra.NumberTheoryRoutines.gcd

object Shor {

  val checkEven: Long => ValidatedNec[String, Long] = { n => if (n % 2 == 0) 2L.validNec else "Not a factor of 2".invalidNec }
  val checkPowerOf: Long => ValidatedNec[String, Long] = { _ => "Not a power of a".invalidNec }
  //TODO - Check if n = a^b for some a,b; Always fails for now

  val checkGCD: ModularUnitaryParams => ValidatedNec[String, Long] = { params =>
    val (common, _) = gcd(params.x, params.N)
    if (common > 1) common.validNec else s"GCD($params.x, $params.N) is one".invalidNec
  }

  val factorFromOrder: Reader[ModularUnitaryParams, ValidatedNec[String, Long] => ValidatedNec[String, Long]] =
    Reader {
      params => {
        foundOrder =>
          foundOrder andThen { r =>
            if (r % 2 == 0 && math.abs(math.pow(params.x, r / 2) % params.N) != 1) {
              val maybeFactors = List(1, -1).map(shift => gcd((math.pow(params.x, r / 2).toLong + shift) % params.N, params.N)._1)
              maybeFactors
                .find {
                  params.N % _ == 0
                }
                .fold[ValidatedNec[String, Long]](s"None of found factors $maybeFactors are correct".invalidNec)(_.validNec)
            }
            else s"Found improper order $r".invalidNec
          }
      }
    }

  val factorViaOrderFinding: ModularExponentiation => Reader[ModularUnitaryParams, ValidatedNec[String, Long]] = {
    modExp =>
      val orderFinding = order(modExp)
      for {
        foundOrder <- Reader(orderFinding) //TODO - refactor order from OrderFinding to Reader
        findFactor <- factorFromOrder
      } yield findFactor(foundOrder)

  }

  //TODO - combine checks above with randomly generated x (in ModularUnitaryParams), to implement running Shor's algorithm

}
