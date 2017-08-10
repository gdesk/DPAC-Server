package model

import java.util.Calendar


/** Represent the current match result, holding some information about the game for leader-board and statistics.
  * Every Player receive one of this at the end of a game.
  *
  * @author manuBottax
  */
trait MatchResult extends Serializable {

/** a boolean describing if the player has won the game or not */
def result: Boolean
def result_=(result: Boolean): Unit
/* the score of the player for the game */
def score: Int
def score_=(score : Int): Unit
/* the date of the game */
def date: Calendar
def date_=(date: Calendar): Unit
}

/** A simple implementation of [MatchResult]
*
* @constructor create the result
*
*/
class MatchResultImpl() extends MatchResult with Serializable {
override var date : Calendar = Calendar.getInstance()
override var result: Boolean = false
override var score: Int = -1

}