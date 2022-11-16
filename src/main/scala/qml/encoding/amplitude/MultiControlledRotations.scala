package qml.encoding.amplitude

import qml.encoding.amplitude.MultipleControlled.{controlledConfigurationGate, dichotomyToControlMap}
import scotty.quantum.gate.StandardGate.RY
import scotty.quantum.gate.{CompositeGate, Gate}
import scotty.quantum.math.Complex
import scotty.quantum.{BitRegister, Superposition}
import utils.MathOps.CrossOps
import utils.bits
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.stringBitRegister

object MultiControlledRotations {

  //Angles of multiple controlled rotations for amplitude encoding
  val RYAngles: Superposition => Int => Int => Double =
    superPosition => {
      val amps = superPosition.state.grouped(2).map { case Array(r, i) => Complex(r, i) }.toArray

      s =>
        j => {

          val numerator = {
            val sumLimit = math.pow(2, s - 1).toInt
            val sumAmps = List.tabulate(sumLimit) { l =>
              val idx = (2 * j - 1) * sumLimit + l
              math.pow(Complex.abs(amps(idx)), 2)
            }.sum
            math.sqrt(sumAmps)
          }

          val denominator = {
            val sumLimit = math.pow(2, s).toInt
            val sumAmps = List.tabulate(sumLimit) { l =>
              val idx = (j - 1) * sumLimit + l
              math.pow(Complex.abs(amps(idx)), 2)
            }.sum
            math.sqrt(sumAmps)
          }

          val aSinArg = numerator / denominator
          2.0 * math.asin(if (!aSinArg.isNaN) aSinArg else 0.0)
        }
    }

  //Block of multiple controlled rotations, acting on particular qubit
  val branchBlock: Superposition => Boolean => Int => List[Gate] =
    superPosition =>
      inverseRot =>
        tQubitNum => {
          val nQubits = superPosition.qubitCount
          val s = nQubits - tQubitNum + 1

          val dichotomyAngle: Int => Double = RYAngles(superPosition)(s)
          val sDichotomies: List[String] = (bits naryCross (tQubitNum - 1)).map(_.mkString)
          val dichotomies: List[BitRegister] = sDichotomies.map(_.encode[BitRegister])

          val dichotomyCRY: ((BitRegister, Int)) => Option[CompositeGate] = {
            case (dichotomy, j) =>
              dichotomyAngle(j + 1) match {
                case 0.0 => None
                case angle =>
                  val rotationAngle = if (!inverseRot) angle else -angle
                  val targetRY = RY(rotationAngle, tQubitNum - 1)
                  Some((controlledConfigurationGate compose dichotomyToControlMap) (dichotomy)(targetRY))
              }
          }

          dichotomies match {
            case Nil =>
              val angle = dichotomyAngle(1)
              if (angle != 0) List(RY(angle, 0)) else Nil
            case ds => ds.zipWithIndex.flatMap(dichotomyCRY)
          }

        }

  //Generates sequence of Gates to create state, given amplitude of qubit superposition, according to Mottonen et al.
  val amplitudeEncodeCascade: Superposition => List[Gate] =
    superPosition => {
      val makeReverseBlock = branchBlock(superPosition)(false)
      val reverseBlock: List[Gate] => List[Gate] = _.reverse
      val makeBlock = reverseBlock compose makeReverseBlock
      val allBlocks = (1 to 3).toList.flatMap(i => makeBlock(i))
      allBlocks
    }


}
