package qroutines.instances.routines.qft

import breeze.numerics.log2
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import org.scalatest.flatspec.AnyFlatSpec
import qroutines.blocks.routine.QuantumRoutine
import qroutines.blocks.routine.QuantumRoutineOutput.LongOutput
import qroutines.blocks.{ControlledUnitaryPower, NModularExponentiation}
import quantumroutines.blocks.CircuitParams.OrderFindingParams
import quantumroutines.qft.ModularUnitaryParams
import scotty.quantum.gate.StandardGate.{CSWAP, SWAP, X}
import scotty.quantum.gate.{CompositeGate, Controlled, Gate}
import scotty.quantum.{BitRegister, Circuit}
import utils.Measure.measureTimes
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.decimalBitRegister


class NOrderFindingTest extends AnyFlatSpec{

  val modularParams: ModularUnitaryParams = ModularUnitaryParams(7, 15)

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

  "Quantum modular exponentiation 4^(2^j) mod 15 using 4 qubits" should "give results, identical to classically computed" in {

    val modFifteenResults = (0 to 7).map { jPower =>
      println("Calculating 4^" + math.pow(2, jPower).toInt + "(mod 15) quantum mechanically...")
      val (resultModExpBinary, _) =
        measureTimes(1000)(Circuit(X(3), modFifteenFourEigenQubits(0)(4)(jPower))).stats.filter { case (_, times) => times != 0 }.head
      val resultModExpDecimal = BitRegister(resultModExpBinary.values.reverse: _*).decodeE[Int, Int](4)
      math.pow(4, math.pow(2, jPower).toInt) % 15 -> (resultModExpBinary, resultModExpDecimal)
    }

    assert(modFifteenResults.forall { case (expDecimal, (_, qmDecimal)) => expDecimal == qmDecimal })
  }

  val mod15: NModularExponentiation = new NModularExponentiation {
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

  "Order of 7 mod 15, computed quantum mechanically" should "give order r=4" in {
    val orderFindingParams = OrderFindingParams(modularParams, mod15)
    val result = QuantumRoutine.run(50)(NOrderFinding)(orderFindingParams)
    assert(result == LongOutput(4L).validNec)
  }
}
