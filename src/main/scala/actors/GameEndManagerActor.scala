package actors

import java.util.Calendar

import akka.actor.UntypedAbstractActor
import model.{MatchResult, MatchResultImpl, User}
import utils.ActorsUtils

import scala.util.parsing.json.JSONObject

/** Actor that manage the result of a match.
  *
  *  @author manuBottax
  */
class GameEndManagerActor extends UntypedAbstractActor {

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "saveMatch" => {

      val score: Int = message.asInstanceOf[JSONObject].obj("score").asInstanceOf[Int]
      val result: Boolean = message.asInstanceOf[JSONObject].obj("result").asInstanceOf[Boolean]
      val date: Calendar = message.asInstanceOf[JSONObject].obj("date").asInstanceOf[Calendar]
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString


      val matchResult: MatchResult = new MatchResultImpl ()

      matchResult.date = date
      matchResult.result = result
      matchResult.score = score

      println(s"Received a new result from $username ")

      context.actorSelection("/user/messageReceiver/userMaster/databaseManager") ! JSONObject(Map[String, Any](
        "object" -> "addResult",
        "username" -> username,
        "result" -> matchResult))
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }
}
