package actors

import akka.actor.{Props, UntypedAbstractActor}
import model.{Client, ClientManager, Match, MatchManager}

import scala.util.parsing.json.JSONObject

/** Actor that manage the people waitnig for a match and the initial configuration of the game.
  *
  *  @author manuBottax
  */
class GameConfigurationManagerActor (val matchManager: MatchManager) extends UntypedAbstractActor {

  var availableRange: List[Range] = _

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "rangesRequest" => {

      println("Request for the available ranges ")

      availableRange = getAvailableRanges

      sender() ! JSONObject(Map[String, Any](
        "object" -> "ranges",
        "list" -> availableRange,
        "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString))

    }

    case "selectedRange" => {

      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val range: Range = message.asInstanceOf[JSONObject].obj("range").asInstanceOf[Range]

      println("range selected: " + range.toString())

      val selectedMatch: Option[Match] = matchManager.getWaitingMatchFor(range).headOption

      if (selectedMatch.isDefined){
        println("Assigned to a match")
        selectedMatch.get.addPlayer (ClientManager.getClient(ip).get)
      }

      else {
        println(" No Available Match for this range")
      }

    }

    case "startGame" => {

      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val playerList: List[Client] = matchManager.getMatchFor(ClientManager.getClient(senderIP).get).involvedPlayer

      var ipList: Set[String] = Set()

      for (x <- playerList) {
          if (x.ipAddress != senderIP) {
            ipList = ipList + x.ipAddress
          }
      }

      sender() ! JSONObject(Map[String, Any](
        "object" -> "otherPlayerIP",
        "playerList" -> ipList,
        "senderIP" -> senderIP ))
    }

    case _ => println("received unknown message")
  }

  //todo: carica i range disponibili
  private def getAvailableRanges: List[Range] = null



}


object GameConfigurationManagerActor {

  /**
    * Create Props for an actor of this type.
    *
    * @param matchManager reference to a common manager for the match.
    * @return a Props for creating this actor.
    */
  def props(matchManager: MatchManager): Props = Props(new GameConfigurationManagerActor(matchManager))
}


