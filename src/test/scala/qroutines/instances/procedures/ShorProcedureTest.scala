package qroutines.instances.procedures

import org.scalatest.flatspec.AnyFlatSpec
import qroutines.blocks.CircuitParams.OrderFindingParams
import qroutines.blocks.modular.ModularUnitaryParams
import qroutines.blocks.procedure.QuantumProcedure
import qroutines.instances.routines.qft.mod15

class ShorProcedureTest extends AnyFlatSpec{

  def isPrime(n: Long): Boolean = (2 to math.sqrt(n).toInt) forall (x => n % x != 0)

  "Prime factor for N=15" should "be prime and factor of N=15" in {
    val modParams = ModularUnitaryParams(11L, 15L)
    val inParams = OrderFindingParams(modParams, mod15)
    val (log, result) = QuantumProcedure.run(50)(ShorProcedure)(modParams)(inParams)
    log.foreach(println)
    assert(result.isRight && result.map{mParams => mParams.N}.exists(isPrime))
  }

}
