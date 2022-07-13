package quantumroutines.blocks

import scotty.quantum.Circuit

case class CircuitWithParams[P <: CircuitParams](circuit: Circuit, params: P)
