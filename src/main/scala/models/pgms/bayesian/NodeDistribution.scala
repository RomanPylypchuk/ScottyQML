package models.pgms.bayesian

import models.pgms.Node
import org.nd4j.linalg.api.ndarray.INDArray

sealed trait NodeDistribution{
  def variable: Node
  def values: INDArray
}

object NodeDistribution{

  case class IndependentPD(variable: Node, values: INDArray) extends NodeDistribution

  case class TabularCPD(variable: Node, evidence: List[Node], values: INDArray) extends NodeDistribution {
    /*
    def valid: Unit ={

      val checkCardinality: (Node, Long) => ValidatedNec[String, Long] =
        {
          (rv, tCard) =>
          if (rv.card == tCard) tCard.validNec
           else ("Dimension of variable " + rv.name + "(" + rv.card + ") is not equal to table dimension " + tCard).invalidNec
        }

      val z = (variable :: evidence).zip(values.shape.toList).map{case (rv, tCard) => checkCardinality(rv, tCard)}
      z.reduceLeft{case (v1, v2) => (v1, v2).mapN()}
    }
     */

  }
}