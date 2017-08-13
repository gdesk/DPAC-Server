package actors

import akka.actor.{ActorSelection, Props, UntypedAbstractActor}
import model.Match
import utils.ActorsUtils

import scala.util.parsing.json.JSONObject

/** Actor that manage the people waitnig for a match and the initial configuration of the game.
  *
  *  @author manuBottax
  */
class GameConfigurationManagerActor extends UntypedAbstractActor {

  private def MIN_PLAYER(v: Int): Int = 3 + 2 * v
  private def MAX_PLAYER(v: Int): Int = 5 + 2 * v


  //todo: controllare se sono i range giusti
  private val availableRange: List[Range] = List( Range(MIN_PLAYER(0), MAX_PLAYER(0)),  // 3 - 5
                                                  Range(MIN_PLAYER(1), MAX_PLAYER(1)),  // 5 - 7
                                                  Range(MIN_PLAYER(2), MAX_PLAYER(2)))  // 7 - 9

  private var waitingMatch: List[Match] = List()




  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "rangesRequest" => {

      //todo: Controllare quali sono i valori dei range nel client

      println("Request for the available ranges")

      //availableRange = getAvailableRanges

      sender() ! JSONObject(Map[String, Any](
        "object" -> "ranges",
        "list" -> availableRange,
        "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString))

    }

      //todo: controllare se fuzniona la creazione di una nuova quando non lo trova
    case "selectedRange" => {

      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val range: Range = message.asInstanceOf[JSONObject].obj("range").asInstanceOf[Range]

      println("range selected: " + range.toString())

      val selectedMatch: Option[Match] = getWaitingMatchFor(range) headOption

      if (selectedMatch.isDefined){
        if (selectedMatch.get.addPlayer(ip)) {
          println("Assigned to a match")
          println("Player in match: " + selectedMatch.get.involvedPlayer.size)

          sender() ! JSONObject(Map[String, Any](
            "object" -> "newPlayerInMatch",
            "match" -> selectedMatch.get ))
        }

        else{
          System.err.println("Player Already in match, cannot add")
        }
      }

      else {
        println("No Available Match for this range, create a new One")
      }

    }


  case "serverIsRunning" => {

    val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

    val currentMatch: Option[Match] = getMatchFor(senderIP)

    if (currentMatch.isDefined) {
      println("Client match found !")
      currentMatch.get.addReadyPlayer(senderIP)

      println("Client startup completed !")

      if (currentMatch.get canStart) {
        println("Client startup completed, can start !")
        sender() ! JSONObject(Map[String, Any](
          "object" -> "clientCanConnect",
          "senderIP" -> senderIP
        ))

      }
    }

    else {
      println("MEGAERROREENORME! un Client non connesso sta cercando di iniziare una partita")
      //throw new MatchNotFoundException
    }


  }

    case "updateMatch" =>{
      val currentMatch: Match = message.asInstanceOf[JSONObject].obj("match").asInstanceOf[Match]
      val m = getMatchFor(currentMatch.involvedPlayer.headOption.get)

      m.get.involvedPlayer = currentMatch.involvedPlayer
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }


  private def getWaitingMatchFor(size: Range): List[Match] = {

    val matches: List[Match] = waitingMatch.filter((x) => size == x.size)

    if (matches.isEmpty) {
      waitingMatch = waitingMatch ::: List(new Match(List(),size))
      return waitingMatch
    }

    matches
  }

  private def getMatchFor(clientIP: String): Option[Match] = {

    for ( x <- waitingMatch) {
      val l: List[String] = x.involvedPlayer.filter((x) => x == clientIP)
      if (l.nonEmpty){
        return Option(x)
      }
    }
    Option.empty[Match]
  }



}


