package me.cassayre.florian.comptebon

import me.cassayre.florian.comptebon.expression._
import org.scalatest.FunSuite

import scala.util.Random

import me.cassayre.florian.comptebon.expression.ExpressionParser._

class InlineExpressionParserSuite extends FunSuite {

  implicit def string2Seq(s: String): Seq[Char] = s.toCharArray.toSeq

  test("some correct inputs for the parser") {
    val expressionsMap = Map(
      "1+2" -> Addition(Number(1), Number(2)),
      "1+2-3" -> Subtraction(Addition(Number(1), Number(2)), Number(3)),
      "6/2+1" -> Addition(Division(Number(6), Number(2)), Number(1)),
      "1+2*3" -> Addition(Number(1), Multiplication(Number(2), Number(3))),
      "2-3*4/6" -> Subtraction(Number(2), Division(Multiplication(Number(3), Number(4)), Number(6))),
      "((1+1))" -> Addition(Number(1), Number(1)),
      "\t\n1 + \t2 \r\n  " -> Addition(Number(1), Number(2)),
      "(1)*2" -> Multiplication(Number(1), Number(2)),
      "42" -> Number(42)
    )

    expressionsMap.foreach{ case (k, v) => assert(parseExpression(k).get == v) }
  }

  test("some incorrect inputs for the parser") {
    val expressions = Seq(
      "(1+1",
      ")1+1",
      "1+1)",
      "1+1(",
      "(1+())",
      "1+1()",
      "()",
      "(",
      ")",
      "",
      " ",
      "1 2",
      "1(+)2",
      "(*)",
      "(+1)",
      "((+)1)",
      "((+)-1)",
      "(1(+)-1)"
    )

    expressions.foreach(e => assert(parseExpression(e).isEmpty))
  }

  test("inputs and their result") {
    val expressions = Map(
      "1+2+3+4+5" -> 15,
      "1*2+8/4-1" -> 3,
      "1+2*3+9/3-(2+1)/3" -> 9,
      "(7*8+1)-2*3/2/3" -> 56,
      "((((((((((((((((((((1*9*8))))))))))))))))))))" -> 72
    )

    expressions.foreach{ case (k, v) => assert(parseExpression(k).get.value == v) }
  }

  test("no exception for random inputs") {
    val str = "0123456789+-*/-()"

    val random = new Random(42)

    for {
      i <- 0 until 100000
    } yield {
      val s = (for {
        j <- 0 until random.nextInt(10)
      } yield str.charAt(random.nextInt(str.length))).mkString
      //println(s)
      parseExpression(s)
    }
  }

}
