import scotty.quantum.{QubitRegister, Superposition}

object SuperpositionFactory {

  def apply(nQubits: Int, realAmpIdxMap: Map[Int, Double]): Superposition = {
      val nAmps = math.pow(2, nQubits).toInt

      val qubitRegister = QubitRegister("0" * nQubits)
      val scottyAmplitudes = Array.tabulate(nAmps) { i =>
        Array(realAmpIdxMap.getOrElse(i, 0.0).toFloat, 0.0f)
      }.flatten

      Superposition(qubitRegister, scottyAmplitudes)
  }

  def apply(realAmpStrMap: Map[String, Double]): Option[Superposition] = {
    if (realAmpStrMap.isEmpty) None else {
      val realAmpIdxMap: Map[Int, Double] = realAmpStrMap.map {
        case (strKey, amp) => (Integer.parseInt(strKey, 2), amp)
      }
      val nQubits = realAmpStrMap.head._1.length
      Some(apply(nQubits, realAmpIdxMap))
    }
  }

  def apply(realAmps: Array[Double]): Option[Superposition] = {
    if (realAmps.isEmpty) None else {
      val realAmpIdxMap = realAmps.zipWithIndex.map(_.swap).toMap
      val nQubits = (math.log(realAmps.length) / math.log(2)).toInt
      Some(apply(nQubits, realAmpIdxMap))
    }
  }

}
