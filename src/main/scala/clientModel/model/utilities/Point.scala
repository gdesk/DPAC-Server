package clientModel.model.utilities

/** The position of an Item in the playground.
  *
  * @tparam T the measure unit used on x axis.
  * @tparam W the measure unit used on y axis.
  * @author manuBottax
  */
trait Point [T,W] {

  /** the position of the item on x axis. */
  def x: T

  /** the position of the item on y axis. */
  def y: W
}

/**
  * A point that represent [[Point]] in a virtual playground
  *
  * @constructor create a new Point object.
  *
  * @param x the position of the item on x axis.
  * @param y the position of the item on y axis.
  */
case class PointImpl [T,W] (var x: T, var y: W) extends Point [T,W] {}
