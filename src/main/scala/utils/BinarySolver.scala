package utils

import breeze.linalg.{DenseMatrix, DenseVector, Transpose}
import scotty.quantum.BitRegister
import utils.MathOps.{CrossOps, zip}
import utils.codec.BiCodec.BiCodecSyntax
import utils.factory.BitRegisterFactory.bitBitRegister

import scala.annotation.tailrec

object BinarySolver {

  def augmentedPartition(cond: (Array[Int], Int) => Boolean)
                        (a: Array[Array[Int]], b: Array[Int]) = {

    val (singletonPairs, restPairs) = a.indices.map{ rowI =>
        (a(rowI), b(rowI))
    }.toList.partition { case (row, out) => cond(row, out) }

    val List(singletons, rests) = List(singletonPairs, restPairs).map{batch =>
      val (m, v) = batch.unzip
      (m.toArray, v.toArray)
    }
    (singletons, rests)
  }

  @tailrec
  def binarySolver(n: Int, systemM: Array[Array[Int]], systemO: Array[Int], vars: Map[Int, Int] = Map.empty): Map[Int, Int] = {

    if (vars.keys.size == n) vars
     else {

      val partition = augmentedPartition((row, _) => row.sum == 1) _
      val (singletons, rests) = partition(systemM, systemO)

      if (singletons._1.nonEmpty) {

        val addVars = zip(singletons).map {
          case (singleton, out) =>
            singleton.indexWhere(_ == 1) -> out
        }.toMap
        val newVars = vars ++ addVars

        println("======")
        println(newVars)


        val z = zip(rests).map {
          case (row, out) =>

            val (dimRow, rowAcc) = row.zipWithIndex.foldLeft((Array.fill(row.length)(0), 0)) {
              case ((newRow, acc), (x, i)) =>
                newVars.get(i).fold((newRow.updated(i, x), acc)) {
                  xi =>
                    (newRow, (acc + xi) % 2)
                }
            }

            (dimRow, (out + rowAcc) % 2)
        }

        val (reducedM, reducedO) = z.unzip

        reducedM.map(_.toList).foreach(println)
        println(reducedO.toList)
        println("Iterating again...")

        binarySolver(n, reducedM, reducedO, newVars)
      }
       else{
        //TODO - finish this
        Map.empty
        /*
        val nonZeroRests = zip(rests).filter{case (row, _) => row.sum != 0}
        nonZeroRests match{
          case Array((row, out)) =>
            if (vars.exists{case (_, xi) => xi == 1}) {

              val addVars = row.zipWithIndex.collect{case (xi, i) if xi == 1 => i -> 0}.toMap
              vars ++ addVars
            }
          case more =>
        }
        */
      }


    }
  }

  val binarySolverBruteForce: DenseMatrix[Int] => Option[BitRegister] = {
    system =>

      val satisfiesAll: DenseVector[Int] => Boolean = {
        dichotomy =>
          (0 until system.rows).map{
            rowI =>
              system(rowI, ::)
          }.forall(_.inner.dot(dichotomy) % 2 == 0)
      }

      val allDichotomies = List(0,1).naryCross(system.cols)
      allDichotomies.tail.find(
        dichotomy => satisfiesAll(DenseVector(dichotomy :_*))
      ).map(_.encode[BitRegister])
  }

}
