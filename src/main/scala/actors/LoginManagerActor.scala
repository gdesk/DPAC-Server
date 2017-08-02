package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}
import model.User

import scala.util.parsing.json.JSONObject

/** Actor that manage the login of a user to the system.
  *
  * @author manuBottax
  */
class LoginManagerActor extends UntypedAbstractActor {

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "login" => {
      println("An User want to login !")
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val password: String = message.asInstanceOf[JSONObject].obj("password").toString
      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      if ( checkUsername(username) && checkPassword(password)){
        val user: User = getUserfromDB(username)
        //addOnlineUser(user)
        println(s"The user $username successfully log in !")
        sender() ! JSONObject( Map[String, String](
                   "object" -> "loginResult",
                   "result" -> "success",
                   "username" -> username,
                   "senderIP" -> senderIP ))
      }
      else {
        println(s"Login for user $username has failed")
        sender() ! JSONObject(Map[String, String](
                   "object" -> "loginResult",
                   "result" -> "fail",
                   "username" -> username,
                   "senderIP" -> senderIP ))
      }
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  //todo
  private def checkUsername(username: String): Boolean = true

  //todo
  private def checkPassword(password: String): Boolean = true

  //todo
  private def getUserfromDB(usernaem: String): User = null

  ////todo
  //private def addOnlineUser(user:User): Unit = {}

}

