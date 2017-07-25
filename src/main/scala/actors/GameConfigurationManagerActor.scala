package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}

import scala.util.parsing.json.JSONObject

/** Actor that manage the people waitnig for a match and the initial configuration of the game.
  *
  *  @author manuBottax
  */
class GameConfigurationManagerActor extends UntypedAbstractActor {

  var availableRange: List[Range] = _


  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "rangesRequest"  => {

      println("Request for the available ranges ")

      availableRange = getAvailableRanges

      val m = JSONObject(Map[String, Any](
              "object" -> "ranges",
              "list" -> availableRange ,
              "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))

      sender() ! m

    }

    case _  => println ("received unknown message")
  }

  //todo: carica i range disponibili
  private def getAvailableRanges: List[Range] = null

}

