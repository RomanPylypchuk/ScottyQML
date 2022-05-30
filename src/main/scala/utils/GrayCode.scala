package utils

import scotty.quantum.BitRegister
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister


object GrayCode {

  def differenceIndex(binary1: String, binary2: String): Int =
    binary1.zip(binary2).indexWhere{case (b1, b2) => b1 != b2}

  def gray(binary1: BitRegister, binary2: BitRegister): List[BitRegister] = {
    def grayRecur(bin1: String, bin2: String): List[String] = {
      if (bin1 == bin2) Nil
      else{
        val idxDifferent: Int = differenceIndex(bin1, bin2)
        val bin1Next = bin1.updated(idxDifferent, bin2(idxDifferent))
        bin1Next :: grayRecur(bin1Next, bin2)
      }
    }
    val greyInverted = grayRecur(binary1.decode[String].reverse, binary2.decode[String].reverse)
    binary1 :: greyInverted.map(_.reverse.encode[BitRegister])
  }
}
