# ScottyQML

### This repository contains my Scala code for the most important quantum routines.
#### It is a pity that while Scala is already very well adopted for typical industry uses, it still lacks engagement in scientific community and as a result lacks developments for such purposes.

#### My purpose was to:

+ Revisit / learn new algorithms from Nielsen Chuang book
+ Try these algorithms using [Learn Quantum Computation using Qiskit](https://qiskit.org/textbook/preface.html) book
+ Implement and unite all of them in functional style, using Scala/Cats and Scotty library for quantum computing
 

##### Repository also contains few routines for quantum machine learning (e.g. amplitude encoding via rotations or encoding of probabilistic graphical model into quantum state). These are still in very initial stage.

### Quantum routines here:

+ Elementary (Deutsch-Josza, Bernstein-Vazirani, Simon's)
+ QFT-based (QFT and inverse, phase estimation, order finding, Shor)
+ Search (Grover algorithm)

All of these are instances of ```QuantumRoutine```, which basically comprises three parts:
+ ```QuantumRoutineCircuit```, which builds the circuit for routine
+ Specification for qubit measurements
+ ```QuantumRoutineIntepreter```, which maps the measurement statistics to the desired output of the routine

Each part of ```QuantumRoutine``` listed above contains type members for input/output and different ```Reader``` monads, composition of which defines the whole object. I will add more detailed description later.

Since some of these quantum routines are part of a larger algorithm (e.g. Shor for factoring), there is a ```QuantumProcedure``` that allows to combine classical and quantum steps with logging using ```Writer``` Monad, where each ```Kleisli``` arrow can be either classically implemented monadic bind, or one derived from ```QuantumRoutine```.