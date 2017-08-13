package actors

import java.util.Calendar

import akka.actor.{Actor, UntypedAbstractActor}
import model.{ImmutableUser, MatchResult, User}
import utils.ActorsUtils

import scala.util.parsing.json.JSONObject

/** Actor that manage the result of a match and save it.
  *
  *  @author manuBottax
  */
class GameEndManagerActor extends UntypedAbstractActor {

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

      //todo: da aggiustare
    case "matchResult" => {
      val username: String = message.asInstanceOf[JSONObject].obj("user").asInstanceOf[String]
      val result: Boolean = message.asInstanceOf[JSONObject].obj("result").asInstanceOf[Boolean]
      val data: Calendar = message.asInstanceOf[JSONObject].obj("date").asInstanceOf[Calendar]
      val score: Int = message.asInstanceOf[JSONObject].obj("score").asInstanceOf[Int]
      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println(s"Received a new result ($result) from $username ")

      //todo: gestire il result
      context.actorSelection("../databaseManager") ! JSONObject(Map[String, Any](
        "object" -> "addResult",
        "user" -> username,
        "result" -> result,
        "senderIP" -> senderIP ))

      /*sender() ! JSONObject( Map[String, String](
                 "object" -> "resultSaved",
                 "senderIP" -> senderIP))
                 */
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

}
