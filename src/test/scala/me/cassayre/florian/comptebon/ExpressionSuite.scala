package me.cassayre.florian.comptebon

import me.cassayre.florian.comptebon.expression._
import org.scalatest.FunSuite

class ExpressionSuite extends FunSuite {

  test("some valid expressions") {
    val expressions: Seq[Expression] = Seq(
      Number(42),
      Addition(Number(1), Number(2)),
      Subtraction(Number(2), Number(1)),
      Subtraction(Number(3), Number(3)),
      Multiplication(Number(4), Number(7)),
      Division(Number(8), Number(4)),
      Division(Number(27), Number(3)),
      Division(Number(5), Number(1))
    )

    expressions.foreach(e => assert(e.isValid))
  }

  test("some invalid expressions") {
    val expressions: Seq[Expression] = Seq(
      Number(-1),
      Subtraction(Number(1), Number(2)),
      Division(Number(5), Number(0)),
      Division(Number(3), Number(2))
    )

    expressions.foreach(e => assert(!e.isValid))
  }

  test("expressions yield correct result") {
    val expressionsResult: Map[Expression, Int] = Map(
      Number(42) -> 42,
      Addition(Number(3), Addition(Number(4), Number(5))) -> 12,
      Multiplication(Subtraction(Number(10), Number(3)), Addition(Number(2), Number(4))) -> 42,
      Division(Number(17), Addition(Multiplication(Number(4), Number(4)), Number(1))) -> 1
    )

    expressionsResult.foreach{ case (k, v) => assert(k.value == v) }
  }

}
