package actors

import akka.actor.{Actor, UntypedAbstractActor}
import model.User

import scala.util.parsing.json.JSONObject

/** Actor that manage the result of a match and save it.
  *
  *  @author manuBottax
  */
class GameEndManagerActor extends UntypedAbstractActor {

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "matchResult" => {
      val user: User = message.asInstanceOf[JSONObject].obj("user").asInstanceOf[User]
      val result: Int = message.asInstanceOf[JSONObject].obj("result").asInstanceOf[Int] // questo poi sarà uno Score
      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println(s"Received a new result ($result) from $user ")

      saveResultInDB(user, result)

      sender() ! JSONObject( Map[String, String](
                 "object" -> "resultSaved",
                 "senderIP" -> senderIP))
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  //todo
  private def saveResultInDB(user: User, result: Int): Unit = {}

}
