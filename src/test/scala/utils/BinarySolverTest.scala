package utils
import utils.BinarySolver.binarySolver

object BinarySolverTest extends App{
  //val m1 = Array(Array(1, 0, 1), Array(0, 0, 1))
  //val o1 = Array(1, 0)
  //val z = binarySolver(3, m1, o1)
  //println(z)

  /*val m2 = Array(
    Array(1,1,0),
    Array(0,1,1),
    Array(1,0,0)
  )*/
  //val o2 = Array(0,0,0)

  //println(binarySolver(3, m2, o2))

  val m3 = Array(
    Array(0,1,1),
    Array(0,0,0),
    Array(1,0,0)
  )

  val o3 = Array(0,0,0)

  println(binarySolver(3, m3, o3))
}
