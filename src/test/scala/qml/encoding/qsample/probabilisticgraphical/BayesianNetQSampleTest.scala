package qml.encoding.qsample.probabilisticgraphical

import models.pgms.Node
import models.pgms.bayesian.BayesianNet
import models.pgms.bayesian.NodeDistribution.{IndependentPD, TabularCPD}
import org.nd4j.linalg.factory.Nd4j
import scotty.quantum.{BitRegister, Circuit, One}
import utils.Measure
import utils.algebra.precision.Precision
import utils.algebra.precision.Precision.AlmostEquals
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory._

object BayesianNetQSampleTest extends App {

  val measure = Measure.measureTimes(5000)
  implicit val precision: Precision = Precision(1e-1)

  //====================
  val trivialOneNodeNet: BayesianNet = BayesianNet(
    IndependentPD(
      Node("Var1", 2),
      Nd4j.create(Array(0.8, 0.2))
    ) :: Nil
  )

  val trivialOneNodeCircuit: Circuit = BayesianNetQSample.encode(trivialOneNodeNet)
  val oneNodeResults: Map[BitRegister, Int] = measure(trivialOneNodeCircuit).stats.toMap
  val probOne = oneNodeResults(BitRegister(One())).toDouble / 5000
  println(probOne)
  assert(probOne ~= 0.2)

  //=====================
  val rain: Node = Node("Rain", 2)
  val sprinkler: Node = Node("Sprinkler", 2)
  val grass: Node = Node("Grass", 2)

  val exampleRSGNet: BayesianNet = BayesianNet(
    List(
      IndependentPD(
        rain, Nd4j.create(Array(0.8, 0.2))
      ),
      TabularCPD(
        sprinkler,
        List(rain),
        Nd4j.create(Array(0.6, 0.99, 0.4, 0.01)).reshape(2, 2)
      ),
      TabularCPD(
        grass,
        List(rain, sprinkler),
        Nd4j.create(Array(1.0, 0.1, 0.2, 0.01, 0.0, 0.9, 0.8, 0.99)).reshape(2,2,2)
      )
    )
  )

  val RSGNetCircuit: Circuit = BayesianNetQSample.encode(exampleRSGNet)
  val RSGNetResults = measure(RSGNetCircuit).stats.toMap
  val probs = RSGNetResults.map{case (dichotomy, times) => dichotomy -> times.toDouble / 5000}

  probs.foreach(println)
  assert(probs("000".reverse.encode[BitRegister]) ~= 0.48)
  assert(probs("101".reverse.encode[BitRegister]) ~= 0.15)
  assert(probs("011".reverse.encode[BitRegister]) ~= 0.288)
  assert(probs("010".reverse.encode[BitRegister]) ~= 0.032)
}
