package utils

import scotty.quantum._
import scotty.quantum.gate.Gate
import scotty.quantum.gate.StandardGate.X

object BitRegisterFactory {

  implicit class BitRegisterTo(bitRegister: BitRegister) {

    def toControlMap: Map[Int, Bit] =
      bitRegister.values.zipWithIndex.map{case (bit, idx) => (idx, bit)}.toMap

    def toDecimal: Int = Integer.parseInt(this.toHumanString, 2)

    def toHumanString: String = bitRegister.values.map(_.toHumanString.head).mkString

    def toCircuit: Circuit = {
      val nQubits = bitRegister.size
      if (bitRegister.values.forall(_ == Zero()))
        Circuit.apply(Circuit.generateRegister(nQubits))
      else {
        val gates: Array[Gate] = bitRegister.values.zipWithIndex.collect { case (One(_), idx) =>
          X(idx)
        }.toArray
        Circuit(QubitRegister("0" * nQubits), gates: _*)
      }
    }
  }

  //TODO - Typeclass pattern to apply from e.g. String, Map[Int, Bit], etc. Or Codec + imap?
  //Also could use e.g. Int => BitRegister (sort of Reader monad) in toBitRegister, because not all types are
  //enough to figure out number of qubits
  implicit class BitRegisterFrom(str: String) {
    def toBitRegister: BitRegister = BitRegister(str.map(c => Bit(c.asDigit)): _*)
  }

  implicit class BitRegisterSelect(registerParams: (Int, Map[Int, Bit])) {
    def toBitRegister: BitRegister = {
      val (nQubits, controlMap) = registerParams
      val binaryBits: List[Bit] = List.tabulate(nQubits)(i => controlMap.getOrElse(i, Zero()))
      BitRegister(binaryBits :_*)
    }
  }
}
