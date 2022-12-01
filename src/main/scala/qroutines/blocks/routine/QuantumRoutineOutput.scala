package qroutines.blocks.routine

import scotty.quantum.BitRegister

sealed trait QuantumRoutineOutput

object QuantumRoutineOutput{

  sealed trait VectorOutput extends QuantumRoutineOutput
  case class BitStringOutput(b: BitRegister) extends VectorOutput

  sealed trait ScalarOutput extends QuantumRoutineOutput
  sealed trait ConstantOrBalanced extends ScalarOutput
  case object Constant extends ConstantOrBalanced
  case object Balanced extends ConstantOrBalanced

  //sealed trait OneOrTwoToOne extends ScalarOutput
  //case object OneToOne extends OneOrTwoToOne
  //case object TwoToOne extends OneOrTwoToOne

  case class LongOutput(l: Long) extends ScalarOutput
}
