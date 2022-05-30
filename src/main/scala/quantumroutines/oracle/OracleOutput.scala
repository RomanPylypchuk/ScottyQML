package quantumroutines.oracle

import scotty.quantum.BitRegister

sealed trait OracleOutput

object OracleOutput{

  sealed trait ConstantOrBalanced extends OracleOutput
  case object Constant extends ConstantOrBalanced
  case object Balanced extends ConstantOrBalanced

  //case class ScalarOutput(o: ConstantOrBalanced) extends OracleOutput
  case class VectorOutput(b: BitRegister) extends OracleOutput
}

