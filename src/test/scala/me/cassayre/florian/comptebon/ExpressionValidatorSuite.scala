package me.cassayre.florian.comptebon

import me.cassayre.florian.comptebon.expression.ExpressionValidator._
import me.cassayre.florian.comptebon.expression.{Multiplication, Number}
import org.scalatest.FunSuite

class ExpressionValidatorSuite extends FunSuite {

  test("basic scenario") {
    val goal = 155
    val plates = Seq(2, 25, 75, 50, 10, 8)

    val expression = Multiplication(Number(50), Number(10))

    assert(expression.isValid)
    assert(usesPlates(expression, plates))
  }

}
