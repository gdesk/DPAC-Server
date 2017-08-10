package actors

import akka.actor.{ActorSelection, Props, UntypedAbstractActor}
import model.Match

import scala.util.parsing.json.JSONObject

/** Actor that manage the people waitnig for a match and the initial configuration of the game.
  *
  *  @author manuBottax
  */
class GameConfigurationManagerActor extends UntypedAbstractActor {

  private def MIN_PLAYER(v: Int): Int = 3 + 2 * v
  private def MAX_PLAYER(v: Int): Int = 5 + 2 * v




  private val availableRange: List[Range] = List( Range(MIN_PLAYER(0), MAX_PLAYER(0)),  // 3 - 5
                                                  Range(MIN_PLAYER(1), MAX_PLAYER(1)),  // 5 - 7
                                                  Range(MIN_PLAYER(2), MAX_PLAYER(2)))  // 7 - 9

  private var waitingMatch: List[Match] = List()
  private var startedMatch: List[Match] = List()



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

    case "selectedRange" => {

      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val range: Range = message.asInstanceOf[JSONObject].obj("range").asInstanceOf[Range]

      println("range selected: " + range.toString())

      val selectedMatch: Option[Match] = getWaitingMatchFor(range) headOption

      if (selectedMatch.isDefined){
        println("Assigned to a match")
        //TODO: testare se funziona cos' o se devo lavorare sulla lista
        selectedMatch.get.addPlayer(ip)
        println("Player in match: " + selectedMatch.get.involvedPlayer.size)
      }

      else {
        println("No Available Match for this range")
      }

    }

    case "startGame" => {

      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val playerList: List[String] = getMatchFor(senderIP).get.involvedPlayer


      sender() ! JSONObject(Map[String, Any](
        "object" -> "otherPlayerIP",
        "playerList" -> playerList,
        "senderIP" -> senderIP))
    }


  case "serverIsRunning" => {

  val senderIP: String = message.asInstanceOf[JSONObject].obj ("senderIP").toString

    val currentMatch: Option[Match] = getMatchFor(senderIP)

    println("Client match found !")

    if(currentMatch.isDefined) {
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
  println ("MEGAERROREENORME! un Client non connesso sta cercando di iniziare una partita")
    //throw new MatchNotFoundException
  }


  }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  /*
  //todo: carica i range disponibili
  private def getAvailableRanges: List[Range] = {
    if (availableRange.isEmpty) {
      availableRange = availableRange  ::: List(new Range(MIN_PLAYER_RANGE_1,MAX_PLAYER_RANGE_1,1))
    }

    availableRange
  }

  */


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


