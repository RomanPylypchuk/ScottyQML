package qroutines.instances.oracles

import qroutines.blocks.noracle.NOracle

trait GroverOracle extends NOracle{
  val numberSolutions: Int
}
