package qroutines.instances.oracles

import qroutines.blocks.noracle.Oracle

trait GroverOracle extends Oracle{
  val numberSolutions: Int
}
