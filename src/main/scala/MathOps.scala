object MathOps {
  val bits = List(0, 1)

  //Filtered Cartesian product
  def crossJoin[T](list: List[List[T]],
                   combinator:(T, List[T]) => Option[List[T]] = (head: T, tail: List[T]) => Some(head :: tail)): List[List[T]] =
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
