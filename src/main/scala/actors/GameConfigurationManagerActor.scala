package actors

import akka.actor.{ActorSelection, Props, UntypedAbstractActor}
import model.Match

import scala.util.parsing.json.JSONObject

/** Actor that manage the people waitnig for a match and the initial configuration of the game.
  *
  *  @author manuBottax
  */
class GameConfigurationManagerActor extends UntypedAbstractActor {

  private var availableRange: List[Range] = _

  private var waitingMatch: List[Match] = List()
  private var startedMatch: List[Match] = List()



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

      val selectedMatch: Option[Match] = getWaitingMatchFor(range).headOption

      if (selectedMatch.isDefined){
        println("Assigned to a match")
        selectedMatch.get.addPlayer(ip);
      }

      else {
        println("No Available Match for this range")
      }

    }

    case "startGame" => {

      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val playerList: List[String] = getMatchFor(senderIP).involvedPlayer


      sender() ! JSONObject(Map[String, Any](
        "object" -> "otherPlayerIP",
        "playerList" -> playerList,
        "senderIP" -> senderIP))
    }


  case "serverIsRunning" => {

  val senderIP: String = message.asInstanceOf[JSONObject].obj ("senderIP").toString

    val currentMatch: Match = getMatchFor(senderIP)

    currentMatch.addReadyPlayer(senderIP)

    if (currentMatch canStart) {
      sender() ! JSONObject(Map[String, Any](
        "object" -> "clientCanConnect",
        "senderIP" -> senderIP
      ))

    }

  else {
  println ("MEGAERROREENORME! un Client non connesso sta cercando di iniziare una partita")
    //throw new MatchNotFoundException
  }


  }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  //todo: carica i range disponibili
  private def getAvailableRanges: List[Range] = null


  private def getWaitingMatchFor( size: Range): List[Match] = waitingMatch.filter((x) => size == x.size)

  private def getMatchFor(clientIP: String): Match = {
    for ( x <- waitingMatch) {
      val l: List[String] = x.involvedPlayer.filter((x) => x == clientIP)
      if (l.nonEmpty){
        return x
      }
    }
    null
  }

  private def getMatch(id:Int): Option[Match] = {

    val waiting: Option[Match] = waitingMatch.find((x) => x.id == id)

    val started: Option[Match] = startedMatch.find((x) => x.id == id)

    if ( waiting.isDefined) {
      return waiting
    }

    else if (started.isDefined){
      return started
    }

    Option.empty[Match]
  }

  private def getWaitingMatch: List[Match] = {
    waitingMatch
  }

  private def startMatch( selected: Match): Unit = {
    waitingMatch = waitingMatch.filter((x) => x != selected)
    startedMatch = startedMatch ::: List(selected)
  }



}


