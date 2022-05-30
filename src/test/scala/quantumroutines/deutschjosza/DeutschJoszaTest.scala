package quantumroutines.deutschjosza

import quantumroutines.elementary.algorithms.DeutschJosza
import quantumroutines.oracle.OracleOutput.{Balanced, Constant}
import quantumroutines.oracle.instances.DeutschJoszaOracle

object DeutschJoszaTest extends App{

  assert(DeutschJosza.run(1000)(DeutschJoszaOracle.ZeroOracle(3)) == Constant)
  assert(DeutschJosza.run(1000)(DeutschJoszaOracle.OneOracle(3)) == Constant)
  assert(DeutschJosza.run(1000)(DeutschJoszaOracle.BalancedOracle(3)) == Balanced)
}
