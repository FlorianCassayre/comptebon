package me.cassayre.florian.comptebon.expression

object ExpressionValidator {

  def usesPlates(expression: Expression, plates: Seq[Int]): Boolean =
    (expression.numbers intersect plates) == expression.numbers

}
