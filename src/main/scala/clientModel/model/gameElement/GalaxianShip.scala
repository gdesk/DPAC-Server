package clientModel.model.gameElement

import clientModel.model.utilities.Point

/**
  * Created by margherita on 10/07/17.
  */
case class GalaxianShip(override val id: String, override val position: Point[Int, Int]) extends Fruit {
  /**
    * Returns the value that is given as score when Pacman eat that item.
    *
    * @return the score value.
    */
  override def score: Int = GalaxianShip.score

}

object GalaxianShip {
  private val score = 2000
}