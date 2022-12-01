package quantumroutines.elementary.simons

import quantumroutines.elementary.algorithms.Simons
import quantumroutines.oracle.instances.SimonsOracle
import quantumroutines.oracle.instances.SimonsOracle._
import scotty.quantum.BitRegister
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister


object SimonsTest extends App{
  println(Simons.run(1000)(IdentityOracle(3)))

  val threeQubitTwoToOneOracle: SimonsOracle = TwoToOneOracle(3, "110".encode[BitRegister])
  println(Simons.run(1000)(threeQubitTwoToOneOracle))
}
