package clientModel.model.gameElement

import clientModel.model.utilities.Point

/** A block element for the playground, used to build walls.
  *
  * @constructor create a new block with a position in the playground and a dimension.
  * @param position the position in the playground.
  * @author manuBottax
  */
case class Block(override val position: Point[Int, Int]) extends GameItem