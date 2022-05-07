package quantumroutines.deutschjosza

import quantumroutines.deutschjosza.DeutschJosza.runDeutschJosza

object DeutschJoszaTest extends App{

  assert(runDeutschJosza(Oracle.ZeroOracle(3)) == Constant)
  assert(runDeutschJosza(Oracle.OneOracle(3)) == Constant)
  assert(runDeutschJosza(Oracle.BalancedOracle(3)) == Balanced)
}
