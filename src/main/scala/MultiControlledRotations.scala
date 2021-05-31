import BitRegisterFactory.BitRegisterFrom
import scotty.quantum.{BitRegister, Superposition}
import scotty.quantum.gate.StandardGate.{RX, RY, X}
import scotty.quantum.gate.{CompositeGate, ControlGate, Controlled, Gate, TargetGate}
import scotty.quantum.math.Complex
import MathOps.{bits, crossJoin}
import MultipleControlled.{controlledConfigurationGate, dichotomyToControlMap}

object MultiControlledRotations {

  //Amplitude => Branch => Configuration => Angle
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

  val branchBlock: Superposition => Int => List[Gate] =
    superPosition =>
      tQubitNum => {
        val nQubits = superPosition.qubitCount
        val s = nQubits - tQubitNum + 1

        val dichotomyAngle: Int => Double = RYAngles(superPosition)(s)
        val sDichotomies: List[String] = crossJoin(List.fill(tQubitNum - 1)(bits)).map(_.mkString)
        val dichotomies: List[BitRegister] = sDichotomies.map(_.toBitRegister)

        val dichotomyCRY: ((BitRegister, Int)) => Option[CompositeGate] = {
          case (dichotomy, j) =>
            dichotomyAngle(j + 1) match {
              case 0.0 => None
              case angle =>
                val targetRY = RY(angle, tQubitNum - 1)
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

}
