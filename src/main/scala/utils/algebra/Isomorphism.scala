package utils.algebra

trait Isomorphism[Arrow[_, _], A, B] { self =>
  def to: Arrow[A, B]
  def from: Arrow[B, A]
}

object Isomorphism{
  type IsoSet[A, B] = Isomorphism[Function1, A, B]
  type <=>[A, B] = IsoSet[A, B]

  def id[A]: A <=> A = new (A <=> A){
    def to: A => A = a => a
    def from: A => A = a => a
  }
}
