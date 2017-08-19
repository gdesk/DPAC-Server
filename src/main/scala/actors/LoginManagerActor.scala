package actors

import akka.actor.UntypedAbstractActor
import utils.ActorsUtils

import scala.util.parsing.json.JSONObject

/** Actor that manage the login of a user to the system.
  *
  * @author manuBottax
  */
class LoginManagerActor extends UntypedAbstractActor {

  private var pendingUser: List[String] = List()

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

      // receive a login request from a remote client
    case "login" => {
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val password: String = message.asInstanceOf[JSONObject].obj("password").toString
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println(s"An User want to login as $username !")

      pendingUser = pendingUser ::: List(ip)

        // send a request to the database manager to check the login information
      context.actorSelection("../databaseManager") ! JSONObject(Map[String, String](
        "object" -> "checkUsernameAndPassword",
        "username" -> username,
        "password" -> password,
        "senderIP" -> ip))

    }

      // get the response from the database manager and notify the client
    case "checkLoginInfoResult" => {
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val result: String = message.asInstanceOf[JSONObject].obj("result").toString
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      result match {
        case "success" => {

          println(s"The user $username successfully log in !")

          context.parent ! JSONObject( Map[String, String](
            "object" -> "loginResult",
            "result" -> "success",
            "username" -> username,
            "senderIP" -> ip ))
        }

        case _ => {
          println(s"Login for user $username has failed")

          context.parent ! JSONObject(Map[String, String](
            "object" -> "loginResult",
            "result" -> "fail",
            "username" -> username,
            "senderIP" -> ip ))
        }
      }
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }
}

