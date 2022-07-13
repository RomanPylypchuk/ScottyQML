package quantumroutines.blocks

sealed trait CircuitParams

object CircuitParams{
  case class QPEQubits(nPhaseQubits: Int, nEigenQubits: Int) extends CircuitParams
}
