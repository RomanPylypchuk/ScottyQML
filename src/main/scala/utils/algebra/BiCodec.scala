package utils.algebra

import utils.algebra.Isomorphism._

trait BiCodec[A1, T1] {self =>
  def encode(value: A1): T1
  def decode(value: T1): A1

  //Version of invariant functor, parametrized on both types
  def imap2[A2, T2](fa: A1 <=> A2, ft: T1 <=> T2): BiCodec[A2, T2] = new BiCodec[A2, T2] {
    def encode(value: A2): T2 = ft.to(self.encode(fa.from(value)))
    def decode(value: T2): A2 = fa.to(self.decode(ft.from(value)))
  }

  //Same as in imap for Invariant
  def imap[A2](fa: A1 <=> A2): BiCodec[A2, T1] = imap2(fa, id)
}

object BiCodec {

  implicit class BiCodecOps[X](value: X){
    def encode[T](implicit c: BiCodec[X, T]): T = c.encode(value)
    def decode[A](implicit c: BiCodec[A, X]): A = c.decode(value)
  }

  /*
  def encode[A, T](value: A)(implicit c: BiCodec[A, T]): T =
    c.encode(value)

  def decode[A, T](value: T)(implicit c: BiCodec[A, T]): A =
    c.decode(value)
  */
  def unitCodec[T]: BiCodec[T, T] = new BiCodec[T, T] {
    def encode(value: T): T = value
    def decode(value: T): T = value
  }
}
