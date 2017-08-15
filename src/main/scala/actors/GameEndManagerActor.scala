package actors

import akka.actor.UntypedAbstractActor
import model.{MatchResult, User}
import utils.ActorsUtils

import scala.util.parsing.json.JSONObject

/** Actor that manage the result of a match.
  *
  *  @author manuBottax
  */
class GameEndManagerActor extends UntypedAbstractActor {

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    //todo: da testare !
    case "matchResult" => {
      val user: User = message.asInstanceOf[JSONObject].obj("user").asInstanceOf[User]
      val result: MatchResult = message.asInstanceOf[JSONObject].obj("result").asInstanceOf[MatchResult]
      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println(s"Received a new result from ${user.username} ")

      context.actorSelection("../databaseManager") ! JSONObject(Map[String, Any](
        "object" -> "addResult",
        "user" -> user,
        "result" -> result,
        "senderIP" -> senderIP ))
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

}
