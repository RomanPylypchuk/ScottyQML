package qroutines.blocks.routine

import scotty.quantum.BitRegister

sealed trait QuantumRoutineOutput

object QuantumRoutineOutput{

  sealed trait VectorOutput extends QuantumRoutineOutput
  case class BitStringOutput(b: BitRegister) extends VectorOutput

  sealed trait OneOrTwoToOne extends VectorOutput
  case object OneToOne extends OneOrTwoToOne
  case class TwoToOne(b: BitRegister) extends OneOrTwoToOne

  sealed trait ScalarOutput extends QuantumRoutineOutput
  sealed trait ConstantOrBalanced extends ScalarOutput
  case object Constant extends ConstantOrBalanced
  case object Balanced extends ConstantOrBalanced

  case class LongOutput(l: Long) extends ScalarOutput
}
