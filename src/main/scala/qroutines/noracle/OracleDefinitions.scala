package qroutines.noracle

import scotty.quantum.{Bit, BitRegister}

sealed trait OracleDefinitions

object OracleDefinitions{
  case class BitValue(value: Bit) extends OracleDefinitions
  case class BitStringValue(value: BitRegister) extends OracleDefinitions
  case class BitShiftValue(value: Option[Map[Int, Bit]]) extends OracleDefinitions
}
