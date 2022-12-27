package qroutines.blocks.routine

import cats.data.Reader
import cats.data.ValidatedNec
import qroutines.blocks.measurements.QuantumMeasurementBackend.DefaultScottyBackend
import qroutines.blocks.measurements.{QuantumMeasurementBackend, QuantumMeasurementParams, QuantumMeasurementResult}
import quantumroutines.blocks.CircuitParams
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.Circuit
import qroutines.blocks.routine.QuantumRoutineCircuit.DependentQuantumRoutineCircuit

trait QuantumRoutine { self =>
  type InParamsType <: CircuitParams

  val qrCircuit: DependentQuantumRoutineCircuit{type InParamsType = self.InParamsType}
  val qrMeasureQubits: Reader[qrCircuit.usedRoutine.InParamsType, Set[Int]]
  val qrInterpreter: QuantumRoutineInterpreter{type InParamsType = self.InParamsType}

}

object QuantumRoutine{

    def run[Q <: QuantumRoutine](times: Int, backend: QuantumMeasurementBackend = DefaultScottyBackend)
        (qr: Q)(qParams: qr.InParamsType): ValidatedNec[String, qr.qrInterpreter.RoutineOutput] = {

      val routine = for {
        circuit <- qr.qrCircuit.circuit
        usedRoutineParams <- qr.qrCircuit.inParamsToUsedRoutineParams
        measuredQubits = qr.qrMeasureQubits(usedRoutineParams)
        measurements = backend.measure(QuantumMeasurementParams(times, measuredQubits))(circuit)
        interpreter <- qr.qrInterpreter.interpret
      } yield interpreter(measurements)

      routine(qParams)
    }


  /*implicit class QuantumRoutineExecutor[Q <: QuantumRoutine](val qr: Q){
    def run(times: Int)(qParams: qr.InParamsType)
           (implicit backend: QuantumMeasurementBackend): ValidatedNec[String,qr.qrInterpreter.RoutineOutput] = {

      val routine = for {
        circuit <- qr.qrCircuit.circuit
        usedRoutineParams <- qr.qrCircuit.inParamsToUsedRoutineParams
        measuredQubits = qr.qrMeasureQubits(usedRoutineParams)
        measurements = backend.measure(QuantumMeasurementParams(times, measuredQubits))(circuit)
        interpreter <- qr.qrInterpreter.interpret
      } yield interpreter(measurements)

      routine(qParams)
    }
  }*/

}
