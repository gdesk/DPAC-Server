package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}
import model.User

import scala.util.parsing.json.JSONObject

/** Actor that manage the login of a user to the system.
  *
  * @author manuBottax
  */
class LoginManagerActor (val clientMessageDispatcher: ActorRef) extends UntypedAbstractActor {

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "login" => {
      println("An User want to login !")
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val password: String = message.asInstanceOf[JSONObject].obj("password").toString

      if ( checkUsername(username) && checkPassword(password)){
        val user: User = getUserfromDB(username)
        addOnlineUser(user)
        println(s"The user $username successfully log in !")
        sender() ! JSONObject( Map[String, String](
                   "object" -> "loginResult",
                   "result" -> "success",
                   "username" -> username,
                   "senderIp" -> "127.0.0.1" )) //todo: l'ip andrà letto dal json in arrivo
      }
      else {
        println(s"Login for user $username has failed")
        sender() ! JSONObject(Map[String, String](
                   "object" -> "loginResult",
                   "result" -> "fail",
                   "username" -> username,
                   "senderIp" -> "127.0.0.1")) //todo: l'ip andrà letto dal json in arrivo
      }
    }

    case _  => println ("received unknown message")
  }

  //todo
  private def checkUsername(username: String): Boolean = true

  //todo
  private def checkPassword(password: String): Boolean = true

  //todo
  private def getUserfromDB(usernaem: String): User = null

  //todo
  private def addOnlineUser(user:User): Unit = {}

}

object LoginManagerActor {

    /**
      * Create Props for an actor of this type.
      *
      * @param clientMessageDispatcher the reference to the actor that send message to the client
      * @return a Props for creating this actor.
      */
    def props(clientMessageDispatcher: ActorRef): Props = Props(new LoginManagerActor(clientMessageDispatcher))
}

