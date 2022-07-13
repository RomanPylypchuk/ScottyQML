package quantumroutines.qft

import scotty.quantum.Circuit

trait ModularExponentiation {
  def controlPower: ModularUnitaryParams => (Int, Int) => Circuit
}
