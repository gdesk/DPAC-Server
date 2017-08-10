package clientModel.model.gameElement

import clientModel.model.utilities.Point

/**
  * Created by margherita on 10/07/17.
  */
case class Pill(override val id: String, override val position: Point[Int, Int]) extends Eatable {
  /**
    * Returns the value that is given as score when Pacman eat that item.
    *
    * @return the score value.
    */
  override def score: Int = Pill.score

}

object Pill {
  private val score = 50
}