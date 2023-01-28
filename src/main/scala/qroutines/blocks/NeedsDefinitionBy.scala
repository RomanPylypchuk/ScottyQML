package qroutines.blocks

trait NeedsDefinitionBy[+A] {
  type DefiningType <: A

  val definingObject: DefiningType
}
