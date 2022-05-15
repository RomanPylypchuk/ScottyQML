package utils.codec

import utils.algebra.Isomorphism._
import utils.codec.BiCodec.unitCodec

//TODO - perhaps BiCodec is extraneous, it is basically the same as Isomorphism (<=>)
trait BiCodec[A1, T1] {self =>
  def f: A1 <=> T1

  //Version of invariant functor, parametrized on both types
  def imap2[A2, T2](fa: BiCodec[A1, A2], ft: BiCodec[T1, T2]): BiCodec[A2, T2] = new BiCodec[A2, T2] {

    def f: A2 <=> T2 = new (A2 <=> T2){
      def to: A2 => T2 = ft.f.to compose self.f.to compose fa.f.from
      def from: T2 => A2 = fa.f.to compose self.f.from compose ft.f.from
    }
  }

  //Same as in imap for Invariant
  def imap[A2](fa: BiCodec[A1, A2]): BiCodec[A2, T1] = imap2(fa, unitCodec)
}

object BiCodec {

  def apply[A1, T1](arrow: A1 <=> T1): BiCodec[A1, T1] = new BiCodec[A1, T1] {
    def f: A1 <=> T1 = arrow
  }

  implicit class BiCodecSyntax[X](value: X){
    def encode[T](implicit c: BiCodec[X, T]): T = c.f.to(value)
    def decode[A](implicit c: BiCodec[A, X]): A = c.f.from(value)
  }

  def unitCodec[T]: BiCodec[T, T] = new BiCodec[T, T] {
    def f: T <=> T = id
  }
}
