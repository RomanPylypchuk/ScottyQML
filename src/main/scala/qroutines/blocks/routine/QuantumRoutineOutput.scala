package qroutines.blocks.routine

import scotty.quantum.BitRegister
import spire.math.Rational

sealed trait QuantumRoutineOutput

object QuantumRoutineOutput{

  sealed trait VectorOutput extends QuantumRoutineOutput
  case class BitStringOutput(br: BitRegister) extends VectorOutput

  sealed trait OneOrTwoToOne extends VectorOutput
  case object OneToOne extends OneOrTwoToOne
  case class TwoToOne(br: BitRegister) extends OneOrTwoToOne

  sealed trait ScalarOutput extends QuantumRoutineOutput

  case class RationalOutput(x: Rational) extends ScalarOutput

  sealed trait ConstantOrBalanced extends ScalarOutput
  case object Constant extends ConstantOrBalanced
  case object Balanced extends ConstantOrBalanced

  case class LongOutput(l: Long) extends ScalarOutput
}
