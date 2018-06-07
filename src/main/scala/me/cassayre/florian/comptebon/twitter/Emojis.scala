package me.cassayre.florian.comptebon.twitter

object Emojis {

  /**
    * A dart hitting the bullseye of a board.
    */
  val bullseye: String = "\ud83c\udfaf"

  /**
    * A party popper.
    */
  val party: String = "\ud83c\udf89"

  /**
    * A checkbox with a checkmark.
    */
  val checkmark: String = "\u2611"

  /**
    * Transforms an integer into a sequence of stylized digits.
    * @param n the number
    * @return a string with unicode characters
    */
  def intToEmojis(n: Int): String = n.toString.toCharArray.map(c => digit(c.asDigit)).mkString

  private def digit(n: Int): String = {
    require(n >= 0 && n <= 9)
    new String(Array(0x30 + n, 0xe2, 0x83, 0xa3).map(_.toByte))
  }

}
