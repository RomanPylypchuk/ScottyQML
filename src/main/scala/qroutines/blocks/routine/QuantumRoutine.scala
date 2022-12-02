package qroutines.blocks.routine

import cats.data.ValidatedNec
import qroutines.blocks.measurements.{QuantumMeasurementBackend, QuantumMeasurementParams, QuantumMeasurementResult}
import quantumroutines.blocks.CircuitParams
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.Circuit

trait QuantumRoutine { self =>
  type InParamsType <: CircuitParams
  type RoutineCircuitType <: QuantumRoutineCircuit

  val qrCircuit: RoutineCircuitType{type InParamsType = self.InParamsType}
  val qrInterpreter: QuantumRoutineInterpreter{type InParamsType = self.InParamsType}

}

object QuantumRoutine{

  implicit class QuantumRoutineExecutor[Q <: QuantumDependentRoutine](val qr: Q){
    def run(qParams: qr.InParamsType, mParams: QuantumMeasurementParams)
           (implicit backend: QuantumMeasurementBackend): ValidatedNec[String,qr.qrInterpreter.RoutineOutput] = {

      val routine = for {
        circuit <- qr.qrCircuit.circuit
        measurements = QuantumMeasurementResult(backend.measure(mParams)(circuit), mParams)
        interpreter <- qr.qrInterpreter.interpret
      } yield interpreter(measurements)

      routine(qParams)
    }
  }

}
