package clientModel.model

import java.util.Calendar


/** Represent the current match result, holding some information about the game for leader-board and statistics.
  * Every Player receive one of this at the end of a game.
  *
  * @author manuBottax
  */
trait MatchResult {

  /** a boolean describing if the player has won the game or not*/
  def result: Boolean
  /** the score of the player for the game*/
  def score: Int
  /** the date of the game*/
  def date: Calendar
}

/** A simple implementation of [[MatchResult]]
  *
  * @constructor create the result
  * @param result if the player has won the game or not
  * @param score the score of the player for the game
  */
class MatchResultImpl( val result : Boolean, val score: Int) extends MatchResult {

  val date : Calendar = Calendar.getInstance()

}
