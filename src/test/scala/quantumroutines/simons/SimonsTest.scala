package quantumroutines.simons

import quantumroutines.elementary.algorithms.Simons
import quantumroutines.oracle.instances.SimonsOracle
import quantumroutines.oracle.instances.SimonsOracle._
import scotty.quantum.BitRegister
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister


object SimonsTest extends App{
  //val z = Simons.simons(IdentityOracle(3))
  //println(z)
  val threeQubitTwoToOneOracle: SimonsOracle = TwoToOneOracle(3, "110".encode[BitRegister])
  println(Simons.run(1000)(threeQubitTwoToOneOracle))
}
