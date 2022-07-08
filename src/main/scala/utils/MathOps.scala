package utils

object MathOps {

  //Inverse isomap
  def inverseMap[K, V](directMap: Map[K, V]): Map[V, K] =
    directMap.map { case (k, v) => (v, k) }

  //Filtered Cartesian product
  def crossJoin[T](list: List[List[T]],
                   combinator: (T, List[T]) => Option[List[T]] = (head: T, tail: List[T]) => Some(head :: tail)): List[List[T]] = {
    list match {
      case Nil => Nil
      case x :: Nil => x map (List(_))
      case x :: xs =>
        val xsJoin = crossJoin(xs, combinator)
        for {
          i <- x
          j <- xsJoin
          combined <- combinator(i, j)
        } yield combined
    }
  }

  implicit class CrossOps[T](items: List[T]) {
    def naryCross(n: Int): List[List[T]] = crossJoin(List.fill(n)(items))
  }

  def zip[A, B](pair: Tuple2[Array[A], Array[B]]): Array[(A, B)] = {
    val (xs, ys) = pair
    xs.zip(ys)
  }

}
