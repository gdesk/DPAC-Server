package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}
import model.MatchResult

import scala.util.parsing.json.JSONObject

/** Actor that manage the database with user information.
  *
  * @author manuBottax
  */
class DatabaseManagerActor extends UntypedAbstractActor {

  //todo: questo attore interagisce con il db e mi restituisce informazioni sugli utenti => probabilmente servirà un parametro


  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {


    case "getPreviousMatchResult" => {
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString

      val resultList: List[MatchResult] = getMatchResultFor(username)

        sender() ! JSONObject(Map[String, Any](
                                  "object" -> "previousMatchResult",
                                  "list" -> resultList,
                                  "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString  ))
    }

    case "allMatchResult" => self ! JSONObject(Map[String, Any](
          "object" -> "getPreviousMatchResult",
          "username" -> message.asInstanceOf[JSONObject].obj("username").toString,
          "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  //todo
  private def getMatchResultFor(username: String): List[MatchResult] = null

}
