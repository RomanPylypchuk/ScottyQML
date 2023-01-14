package utils.algebra

import breeze.linalg.DenseVector
import utils.codec.BiCodec

object InnerProductMod {

  implicit class InnerProductMod[X](x: X){
    def dot(y: X, modN: Int)(implicit codec: BiCodec[List[Int], X]): Int = {
      val xBinary: DenseVector[Int] = DenseVector(codec.f.from(x) :_*)
      val yBinary: DenseVector[Int] = DenseVector(codec.f.from(y) :_*)
      val innerProd = xBinary.dot(yBinary)
      innerProd % modN
    }
  }
}
