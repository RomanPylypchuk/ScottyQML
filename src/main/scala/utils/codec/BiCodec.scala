package utils.codec

import cats.data.Reader
import utils.algebra.Isomorphism._
import utils.codec.BiCodec.unitCodec

trait BiCodec[A1, T1] {self =>
  def f: A1 <=> T1

  //Version of invariant functor, parametrized on both types
  def map2[A2, T2](fa: BiCodec[A1, A2], ft: BiCodec[T1, T2]): BiCodec[A2, T2] = new BiCodec[A2, T2] {

    def f: A2 <=> T2 = new (A2 <=> T2){
      def to: A2 => T2 = ft.f.to compose self.f.to compose fa.f.from
      def from: T2 => A2 = fa.f.to compose self.f.from compose ft.f.from
    }
  }

  //Same as in imap for Invariant
  def map[A2](fa: BiCodec[A1, A2]): BiCodec[A2, T1] = map2(fa, unitCodec)

  //Lift BiCodec to read from some environment
  def emap2[E, A2, T2](ra: Reader[E, BiCodec[A1, A2]], rt: Reader[E, BiCodec[T1, T2]]): Reader[E, BiCodec[A2, T2]] =
    for{
      isoA <- ra
      isoT <- rt
    } yield map2(isoA, isoT)

  def emap[E, A2](ra: Reader[E, BiCodec[A1, A2]]): Reader[E, BiCodec[A2, T1]] =
    emap2[E, A2, T1](ra, Reader(_ => BiCodec.unitCodec[T1]))
}

object BiCodec {

  def apply[A1, T1](iso: A1 <=> T1): BiCodec[A1, T1] = new BiCodec[A1, T1] {
    def f: A1 <=> T1 = iso
  }

  implicit class BiCodecSyntax[X](value: X){
    def encode[T](implicit c: BiCodec[X, T]): T = c.f.to(value)
    def decode[A](implicit c: BiCodec[A, X]): A = c.f.from(value)

    def encodeE[E, T](e: E)(implicit r: Reader[E, BiCodec[X, T]]): T =
      r.map(biCodec => biCodec.f.to(value)).run(e)

    def decodeE[E, A](e: E)(implicit r: Reader[E, BiCodec[A, X]]): A =
      r.map(biCodec => biCodec.f.from(value)).run(e)
  }

  def unitCodec[T]: BiCodec[T, T] = new BiCodec[T, T] {
    def f: T <=> T = id
  }
}
