package quantumroutines.deutschjosza

import quantumroutines.deutschjosza.DeutschJosza.runDeutschJosza

object DeutschJoszaTest extends App{

  assert(runDeutschJosza(CBOracle.ZeroOracle(3)) == Constant)
  assert(runDeutschJosza(CBOracle.OneOracle(3)) == Constant)
  assert(runDeutschJosza(CBOracle.BalancedOracle(3)) == Balanced)
}
