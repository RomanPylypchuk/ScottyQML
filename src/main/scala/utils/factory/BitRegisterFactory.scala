package utils.factory

import cats.data.Reader
import scotty.quantum._
import scotty.quantum.gate.Gate
import scotty.quantum.gate.StandardGate.X
import utils.algebra.Isomorphism.<=>
import utils.codec.BiCodec
import utils.codec.BiCodec.unitCodec
import utils.paddedIntToBinary

object BitRegisterFactory {
  type BitRegisterCodec[A] = BiCodec[A, BitRegister]

  val bitRegisterUnit: BiCodec[BitRegister, BitRegister] = unitCodec[BitRegister]

  implicit val stringBitRegister: BitRegisterCodec[String] = bitRegisterUnit.map[String](
    BiCodec(new (BitRegister <=> String) {
      def to: BitRegister => String = _.values.map(_.toHumanString.head).mkString
      def from: String => BitRegister = str => BitRegister(str.map(c => Bit(c.asDigit)): _*)
    })
  )

  implicit val bitBitRegister: BitRegisterCodec[List[Int]] = stringBitRegister.map[List[Int]](
    BiCodec(
      new (String <=> List[Int]){
        def to: String => List[Int] = _.map{
          case '0' => 0
          case '1' => 1}.toList
        def from: List[Int] => String = _.mkString
      }
    )
  )

  implicit val decimalBitRegister: Reader[Int, BitRegisterCodec[Int]] = stringBitRegister.emap[Int, Int](
    Reader {
      nQubits =>
        BiCodec(new (String <=> Int) {
          def to: String => Int = Integer.parseInt(_, 2)

          def from: Int => String = paddedIntToBinary(nQubits)
        }
        )
    }
  )

  implicit val controlMapBitRegister: Reader[Int, BitRegisterCodec[Map[Int, Bit]]] = bitRegisterUnit.emap[Int, Map[Int, Bit]](
    Reader {
      nQubits =>
        BiCodec(new (BitRegister <=> Map[Int, Bit]) {
          def to: BitRegister => Map[Int, Bit] = _.values.take(nQubits).zipWithIndex.map { case (bit, idx) => (idx, bit) }.toMap

          def from: Map[Int, Bit] => BitRegister = {
            controlMap =>
              val binaryBits: List[Bit] = List.tabulate(nQubits)(i => controlMap.getOrElse(i, Zero()))
              BitRegister(binaryBits: _*)
          }
        }
        )
    }
  )

  implicit class BitRegisterTo(bitRegister: BitRegister) {

    def reverse: BitRegister = BitRegister(bitRegister.values.reverse :_*)

    //TODO - is it possible to implement this via BiCodec?
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

}
