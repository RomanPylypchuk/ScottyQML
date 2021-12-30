package utils

import scotty.quantum.{Bit, BitRegister}

object BitRegisterFactory {

  val bits = List(0, 1)

  implicit class BitRegisterTo(bitRegister: BitRegister) {
    def toDecimal: Int = Integer.parseInt(this.toHumanString, 2)
    def toHumanString: String = bitRegister.values.map(_.toHumanString.head).mkString
  }

  implicit class BitRegisterFrom(str: String) {
    def toBitRegister: BitRegister = BitRegister(str.map(c => Bit(c.asDigit)): _*)
  }
}
