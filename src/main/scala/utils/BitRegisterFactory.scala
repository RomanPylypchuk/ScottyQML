package utils

import scotty.quantum._
import scotty.quantum.gate.Gate
import scotty.quantum.gate.StandardGate.X

object BitRegisterFactory {

  implicit class BitRegisterTo(bitRegister: BitRegister) {
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

  implicit class BitRegisterFrom(str: String) {
    def toBitRegister: BitRegister = BitRegister(str.map(c => Bit(c.asDigit)): _*)
  }
}
