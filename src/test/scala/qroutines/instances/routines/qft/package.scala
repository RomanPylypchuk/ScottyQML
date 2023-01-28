package qroutines.instances.routines

import breeze.numerics.log2
import qroutines.blocks.ControlledUnitaryPower
import qroutines.blocks.modular.{ModularExponentiation, ModularUnitaryParams}
import scotty.quantum.Circuit
import scotty.quantum.gate.StandardGate.{CSWAP, SWAP, X}
import scotty.quantum.gate.{CompositeGate, Controlled, Gate}

package object qft {

  //First n phase qubits, calculate x^(2^j) mod 15 using 4 qubits
  val modFifteenFourEigenQubits: Int => Int => Int => CompositeGate = {
    n =>
      val swaps: PartialFunction[Int, Vector[Gate]] = {
        case a if Set(2, 13)(a) =>
          Vector(SWAP(n, n + 1), SWAP(n + 1, n + 2), SWAP(n + 2, n + 3))
        case a if Set(7, 8)(a) =>
          Vector(SWAP(n + 2, n + 3), SWAP(n + 1, n + 2), SWAP(n, n + 1))
        case a if Set(4, 11)(a) =>
          Vector(SWAP(n + 1, n + 3), SWAP(n, n + 2))
      }
      x =>
        //"x must be 2,4,7,8,11 or 13"
        //TODO - Ignore checking of x for now
        val multiplyModOnce: Int => Vector[Gate] = swaps andThen { swapGates =>
          if (Set(7, 11, 13)(x)) swapGates ++ (0 to 3).map(i => X(n + i)).toVector
          else swapGates
        }
        j => {
          val repeatGates = List.fill(math.pow(2, j).toInt)(multiplyModOnce(x)).flatten
          val twoPowerTimes = CompositeGate(repeatGates: _*)
          twoPowerTimes
        }
  }

  val mod15: ModularExponentiation = new ModularExponentiation {
    def controlPower: ModularUnitaryParams => ControlledUnitaryPower =
      params => {
        val ModularUnitaryParams(x, modN) = params
        val nEigenQubits: Int = log2(modN.toDouble).ceil.toInt
        val nPhaseQubits: Int = 2 * nEigenQubits + 1

        ControlledUnitaryPower({ case (ci, _) =>
          val modExpBlock = modFifteenFourEigenQubits(nPhaseQubits)(x.toInt)(ci)
          Circuit(
            modExpBlock.gates.map {
              case SWAP(i, j) => CSWAP(ci, i, j)
              case gate => Controlled(ci, gate)
            }: _*)
        })
      }
  }
}
