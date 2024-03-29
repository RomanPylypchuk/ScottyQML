package qroutines

import qml.encoding.amplitude.MultipleControlled.controlledConfigurationGate
import scotty.quantum.QuantumContext.Matrix
import scotty.quantum.gate.DefGate
import scotty.quantum.gate.StandardGate.X
import scotty.quantum.{Bit, BitRegister, Circuit}
import utils.GrayCode.{differenceIndex, gray}
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister
import utils.paddedIntToBinary

object TwoLevelUnitary{

  def twoLevelUnitary(nQubits: Int)(edge1: Int, edge2: Int)(singleGate: Matrix): Circuit = {
    val List(bin1, bin2) = List(edge1, edge2).sorted.map{i =>
       val paddedBinary: String = paddedIntToBinary(nQubits)(i)
       paddedBinary.encode[BitRegister]
      }

    val grayCode = gray(bin1, bin2)

    val grayDeltas = grayCode.sliding(2, 1).map{
      case List(code1, code2) =>
        val diffIdx: Int = differenceIndex(code1.decode[String], code2.decode[String])
        val controlMap: Map[Int, Bit] = code1.values.zipWithIndex.map(_.swap).toMap - diffIdx
        (controlMap, diffIdx)
    }.toList

    val mcNOTs = grayDeltas.init.map{case (controlMap, notIdx) =>
      controlledConfigurationGate(controlMap)(X(notIdx))
    }

    val grey: Circuit = Circuit(mcNOTs :_*)
    val greyReverse: Circuit = Circuit(mcNOTs.reverse :_*)

    val (singleGateDichotomy, singleGateIdx) = grayDeltas.last
    val applyGate = DefGate(singleGate, singleGateIdx)
    val controlledUnitary = controlledConfigurationGate(singleGateDichotomy)(applyGate)

    val twoLevelUnitary = grey combine controlledUnitary combine greyReverse
    twoLevelUnitary
  }

}
