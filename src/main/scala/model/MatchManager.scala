package model

/**
  * Created by Manuel Bottax on 26/07/2017.
  */
object MatchManager {

  private var waitingMatch: List[Match] = List()
  private var startedMatch: List[Match] = List()

  def getWaitingMatchFor( size: Range): List[Match] = waitingMatch.filter((x) => size == x.size)

  def getMatchFor(client: Client): Match = {
    for ( x <- waitingMatch) {
      val l: List[Client] = x.involvedPlayer.filter((x) => x == client)
      if (l.nonEmpty){
        return x
      }
    }
    null
  }

  def getWaitingMatch: List[Match] = {
    waitingMatch
  }

  def startMatch( selected: Match): Unit = {
    waitingMatch = waitingMatch.filter((x) => x != selected)
    startedMatch = startedMatch ::: List(selected)
  }

}
