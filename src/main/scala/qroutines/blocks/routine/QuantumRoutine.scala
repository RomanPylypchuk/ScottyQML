package qroutines.blocks.routine

import cats.data.Reader
import cats.data.ValidatedNec
import qroutines.blocks.measurements.{QuantumMeasurementBackend, QuantumMeasurementParams, QuantumMeasurementResult}
import quantumroutines.blocks.CircuitParams
import quantumroutines.blocks.CircuitParams.NumberQubits
import scotty.quantum.Circuit
import qroutines.blocks.routine.QuantumRoutineCircuit.DependentQuantumRoutineCircuit

trait QuantumRoutine { self =>
  type InParamsType <: CircuitParams
  type RoutineCircuitType <: DependentQuantumRoutineCircuit

  val qrCircuit: RoutineCircuitType{type InParamsType = self.InParamsType}
  val qrMeasureQubits: Reader[qrCircuit.usedRoutine.InParamsType, Set[Int]]
  val qrInterpreter: QuantumRoutineInterpreter{type InParamsType = self.InParamsType}

}

object QuantumRoutine{

  implicit class QuantumRoutineExecutor[Q <: QuantumRoutine](val qr: Q){
    def run(qParams: qr.InParamsType, times: Int)
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
  }

}