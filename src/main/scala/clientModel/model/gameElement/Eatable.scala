package clientModel.model.gameElement

/**
  * Trait that specify an item as eatable by Pacman.
  */
trait Eatable extends GameItem {

    /**
      * Returns the value that is given as score when Pacman eat that item.
      *
      * @return the score value.
      */
    def score: Int

    /**
      * Returns the identifier of the eatable object.
      *
      * @return the object's identifier.
      */
    def id: String
}

//case class EatableImpl(override val id: String, override val position: Point[Int, Int]) extends Eatable {
//}




