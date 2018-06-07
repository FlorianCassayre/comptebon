package me.cassayre.florian.comptebon.game

import scala.util.Random

object GameGenerator {

  val count = 6

  val plates: Seq[Int] = {
    val small = 1 to 10
    val big = Seq(25, 50, 75, 100)

    small ++ small ++ big
  }

  def drawPlates(): Seq[Int] =
    (0 until count).foldLeft((plates, Seq[Int]())) { case ((platesLeft, acc), _) =>
      val (left, right) = platesLeft.splitAt(Random.nextInt(platesLeft.size))
      (left ++ right.tail, right.head +: acc)
    }._2

  def drawTarget(): Int = {
    val range = 101 to 999
    range(Random.nextInt(range.size))
  }

  def drawGame(): GameRound = GameRound(drawTarget(), drawPlates())

}
