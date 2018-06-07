package me.cassayre.florian.comptebon

import me.cassayre.florian.comptebon.game.GameGenerator._
import org.scalatest.FunSuite

class GeneratorSuite extends FunSuite {

  test("there are exactly 6 plates") {
    (0 until 100).forall(_ => drawPlates().lengthCompare(6) == 0)
  }

  test("plates are in the right quantity") {
    (0 until 100).forall(_ => {
      val plates = drawPlates()
      plates.groupBy(identity).forall { case (k, v) => if (k <= 10) v.lengthCompare(2) <= 0 else v.lengthCompare(1) <= 0 }
    })
  }

}
