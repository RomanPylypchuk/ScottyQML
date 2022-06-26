package models.pgms.bayesian

import models.pgms.Node


case class BayesianModel(varSeq: List[NodeDistribution]){

  def nodeNumber: Map[Node, Int] = varSeq.map(_.variable).zipWithIndex.toMap
  def allNodes: Set[Node] = varSeq.map(_.variable).toSet
  //def nodesOrder =
  //def neededTables: Map[Node, List[Node]] = edges.groupMap(_.to)(_.from)

  //Do we need edges for now?
  //Perhaps consider the graph with r.v's and edges in separate class? edges: List[Edge]
  //Check edges -> conditionals correctness using .tables method here and .valid method on TabularCPD
}
