package qroutines

trait NeedsDefinitionBy[+A] {
  type DefiningType <: A

  val definingObject: DefiningType
}
