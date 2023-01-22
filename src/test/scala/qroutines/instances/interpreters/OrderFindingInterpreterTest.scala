package qroutines.instances.interpreters

import cats.data.ValidatedNec
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import org.scalatest.flatspec.AnyFlatSpec
import quantumroutines.qft.ModularUnitaryParams
import scotty.quantum.BitRegister
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.decimalBitRegister

class OrderFindingInterpreterTest extends AnyFlatSpec{

  val modularParams: ModularUnitaryParams = ModularUnitaryParams(7, 15)

  "Order estimate for phase estimates 1536,512" should "result in r=4 (for x=7, N=15)" in {
    val mBinaryPhase1: BitRegister = 1536.encodeE[Int, BitRegister](11)
    val mBinaryPhase2: BitRegister = 512.encodeE[Int, BitRegister](11)
    val List(estimate1, estimate2) = List(mBinaryPhase1, mBinaryPhase2).map(OrderFindingInterpreter.statsConvergent(_))
    val combineTwoEstimates = OrderFindingInterpreter.combineOrders(modularParams)
    val order: ValidatedNec[String, Long] = combineTwoEstimates(estimate1, estimate2)
    assert(order == 4L.validNec)
  }
}
