package me.cassayre.florian.comptebon

package object expression {

  sealed abstract class Expression {

    def isValid: Boolean

    def value: Int

    def numbers: Seq[Int]

  }

  case class Number(n: Int) extends Expression {
    override def isValid: Boolean = n >= 0
    override def value: Int = n
    override def numbers: Seq[Int] = Seq(n)
  }

  abstract class Operator(a: Expression, b: Expression) extends Expression {
    override def isValid: Boolean = a.isValid && b.isValid
    def operator: (Int, Int) => Int
    override def value: Int = operator(a.value, b.value)
    override def numbers: Seq[Int] = a.numbers ++ b.numbers
  }

  case class Addition(a: Expression, b: Expression) extends Operator(a, b) {
    override def operator: (Int, Int) => Int = _ + _
  }

  case class Subtraction(a: Expression, b: Expression) extends Operator(a, b) {
    override def isValid: Boolean = super.isValid && a.value - b.value >= 0
    override def operator: (Int, Int) => Int = _ - _
  }

  case class Multiplication(a: Expression, b: Expression) extends Operator(a, b) {
    override def operator: (Int, Int) => Int = _ * _
  }

  case class Division(a: Expression, b: Expression) extends Operator(a, b) {
    override def isValid: Boolean = super.isValid && b.value != 0 && a.value % b.value == 0
    override def operator: (Int, Int) => Int = _ / _
  }

}
