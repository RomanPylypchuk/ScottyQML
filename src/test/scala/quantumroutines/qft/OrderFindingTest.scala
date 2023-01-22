package quantumroutines.qft

import breeze.numerics.log2
import quantumroutines.blocks.CircuitParams.QPEQubitsOld
import quantumroutines.blocks.CircuitWithParams
import quantumroutines.qft.OrderFinding.qpeOrder
import scotty.quantum.ExperimentResult.StateStats
import scotty.quantum.gate.StandardGate.{CSWAP, SWAP, X}
import scotty.quantum.gate.{CompositeGate, Controlled, Gate}
import scotty.quantum.{BitRegister, Circuit}
import utils.Measure.measureTimes
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.decimalBitRegister

object OrderFindingTest extends App{

  //StatsConvergent Test; TODO - fix it
  val measurementOutcome1 = StateStats(List(
    1536.encodeE[Int, BitRegister](11) -> 600,
  ))

  println(measurementOutcome1)

  val estimate1 = OrderFinding.statsConvergent(measurementOutcome1.stats.head._1)
  println(estimate1)

  val measurementOutcome2 = StateStats(List(
    512.encodeE[Int, BitRegister](11) -> 400
  ))

  val estimate2 = OrderFinding.statsConvergent(measurementOutcome2.stats.head._1)
  println(estimate2)

  val combineTwoEstimates = OrderFinding.combineOrders(ModularUnitaryParams(7, 15))
  println(combineTwoEstimates(estimate1, estimate2))

  io.StdIn.readLine()

  println("Testing mod exp N=15...")
  /////////////////////////////////////////////////////////////////////

  //nEigenQubits = 4
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

  val modFifteenResults = (0 to 5).map { jPower =>
    println("Calculating 4^" + math.pow(2, jPower).toInt + "(mod 15) quantum mechanically...")
    val (resultModExpBinary, _) = measureTimes(1000)(Circuit(X(3), modFifteenFourEigenQubits(0)(4)(jPower))).stats.filter { case (_, times) => times != 0 }.head
    val resultModExpDecimal = BitRegister(resultModExpBinary.values.reverse: _*).decodeE[Int, Int](4)
    math.pow(4, math.pow(2, jPower).toInt) % 15 -> (resultModExpBinary, resultModExpDecimal)
  }

  modFifteenResults.foreach(println)
  assert(modFifteenResults.forall { case (expDecimal, (_, qmDecimal)) => expDecimal == qmDecimal })

  io.StdIn.readLine()
  /////////////////////////////////////////////////////////////////////////////////////////////////

  val mod15 = new ModularExponentiation {
    def controlPower: ModularUnitaryParams => (Int, Int) => Circuit =
    params => {
      val ModularUnitaryParams(x, modN) = params
      val nEigenQubits: Int = log2(modN.toDouble).ceil.toInt
      val nPhaseQubits: Int = 2 * nEigenQubits + 1
      (ci, _) => {
        val modExpBlock = modFifteenFourEigenQubits(nPhaseQubits)(x.toInt)(ci)
        Circuit(
          modExpBlock.gates.map{
            case SWAP(i, j) => CSWAP(ci, i, j)
            case gate => Controlled(ci, gate)
          }
            :_*
        )
      }}
  }

  val params = ModularUnitaryParams(7, 15)
  val mod15Gates: CircuitWithParams[QPEQubitsOld] = qpeOrder(params)(mod15)
  println(OrderFinding.order(mod15)(params))

}
