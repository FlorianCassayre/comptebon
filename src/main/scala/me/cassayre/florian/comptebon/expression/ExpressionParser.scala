package me.cassayre.florian.comptebon.expression

import scala.util.parsing.combinator.JavaTokenParsers


private class ExpressionParser extends JavaTokenParsers {

  lazy val expr: Parser[Expression] = addSub

  lazy val addSub: Parser[Expression] =
    mulDiv ~ rep(
      "+" ~> mulDiv ^^ (e => Addition(_: Expression, e)) |
        "-" ~> mulDiv ^^ (e => Subtraction(_: Expression, e))
    ) ^^ { case head ~ tail => tail.foldLeft(head)((acc, elem) => elem(acc)) }

  lazy val mulDiv: Parser[Expression] =
    atom ~ rep(
      "*" ~> atom ^^ (e => Multiplication(_: Expression, e)) |
        "/" ~> atom ^^ (e => Division(_: Expression, e))
    ) ^^ { case head ~ tail => tail.foldLeft(head)((acc, elem) => elem(acc)) }

  lazy val atom: Parser[Expression] = (
    "[1-9][0-9]*".r ^^ (n => Number(n.toInt))
      | "(" ~> addSub <~ ")" ^^ identity
    )

  def parse(str: String): Option[Expression] = {
    parseAll(expr, str) match {
      case Success(expression, _) => Some(expression)
      case NoSuccess(_, _) => None
    }
  }

}

object ExpressionParser {

  private val parser = new ExpressionParser

  def parseExpression(str: String): Option[Expression] = parser.parse(str)

}
