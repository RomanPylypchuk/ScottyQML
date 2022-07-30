package quantumroutines.oracle

import scotty.quantum.Circuit

trait Oracle {
  def nOracleQubits: Int
  def oracle: Circuit
}
