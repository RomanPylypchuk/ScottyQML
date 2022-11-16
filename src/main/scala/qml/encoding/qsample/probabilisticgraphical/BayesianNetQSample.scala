package qml.encoding.qsample.probabilisticgraphical

import models.pgms.bayesian.BayesianNet
import models.pgms.bayesian.NodeDistribution.{IndependentPD, TabularCPD}
import org.nd4j.linalg.api.ndarray.INDArray
import qml.encoding.amplitude.MultipleControlled.controlledConfigurationGate
import scotty.quantum.gate.Gate
import scotty.quantum.gate.StandardGate.RY
import scotty.quantum.{Bit, Circuit}
import utils.MathOps.CrossOps
import utils.bits

object BayesianNetQSample {

  val probabilityToAngle: Double => Double =
    probabilityOne => 2.0 * math.asin(math.sqrt(probabilityOne))

  val encode: BayesianNet => Circuit = {
    bNet =>
      val gates = bNet.varSeq.flatMap {

        case IndependentPD(variable, values) =>
          val qubit: Int = bNet.nodeNumber(variable)
          val probabilityOne: Double = values.getDouble(1L)
          val angle: Double = probabilityToAngle(probabilityOne)
          RY(angle, qubit) :: Nil

        case TabularCPD(variable, evidence, values) =>
          val targetQubit: Int = bNet.nodeNumber(variable)
          val condProbs: INDArray = values.tensorAlongDimension(1, 1 until values.shape.length : _*)

          val oneTableRotations: List[Gate] = (bits naryCross condProbs.shape.length).map{dichotomy =>
            val dichotomyProb: Double = condProbs.getDouble(dichotomy :_*)
            val targetRotation: RY = RY(probabilityToAngle(dichotomyProb), targetQubit)
            val controlMap: Map[Int, Bit] =
              evidence.zip(dichotomy).map{case (node, bit) => bNet.nodeNumber(node) -> Bit(bit)}.toMap

            controlledConfigurationGate(controlMap)(targetRotation)
          }

        oneTableRotations
      }

      Circuit(gates :_*)
  }

}
