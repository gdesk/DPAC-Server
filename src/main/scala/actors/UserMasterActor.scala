package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}
import utils.ActorsUtils

import scala.util.parsing.json.JSONObject

/** An actor that handle the user-related message and dispatch it to the various actor.
  * It also initialize the user-related actors.
  *
  * @param clientMessageDispatcher: a reference to the actor that dispatch message to the remote client.
  *
  * @author manuBottax
  */
class UserMasterActor (val clientMessageDispatcher: ActorRef) extends UntypedAbstractActor{

  var databaseManager: ActorRef = _
  var loginManager: ActorRef = _
  var registrationManager: ActorRef = _

  override def preStart(): Unit = {

    databaseManager = context.actorOf(Props[DatabaseManagerActor] , "databaseManager")
    println("[ Database manager actor creation completed ]")
    loginManager = context.actorOf(Props[LoginManagerActor], "loginManager")
    println("[ Login manager actor creation completed ]")
    registrationManager = context.actorOf(Props[RegistrationManagerActor] , "registrationManager")
    println("[ Registration manager actor creation completed ]")

  }

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "newUser" => registrationManager ! message

    case "login" => loginManager ! message

    case "logout" => clientMessageDispatcher ! message

    case "allMatchResult" => databaseManager ! message

    ///////// LOCAL MESSAGE HANDLER ////////////////

    case "registrationResult" => clientMessageDispatcher ! message

      // login result handler
    case "loginResult" => {
      val result: String = message.asInstanceOf[JSONObject].obj("result").toString
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      result match {

        case "success" => {

          clientMessageDispatcher ! JSONObject(Map[String, Any](
                    "object" -> "addOnlinePlayer",
                    "username" -> message.asInstanceOf[JSONObject].obj("username").toString,
                    "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))

          println(s"Player $username connected from $ip !" )


          databaseManager ! JSONObject(Map[String, String](
            "object" -> "getPreviousMatchResult",
            "username" -> message.asInstanceOf[JSONObject].obj("username").toString,
            "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString))
        }

        case _ => {
          clientMessageDispatcher ! JSONObject(Map[String, String](
            "object" -> "loginError",
            "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString))
        }
      }
    }

    case "previousMatchResult" => clientMessageDispatcher ! message

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }
}

object UserMasterActor {

  /**
    * Create Props for an actor of this type.
    *
    * @param clientMessageDispatcher the reference to the actor that send message to the client
    * @return a Props for creating this actor.
    */
  def props(clientMessageDispatcher: ActorRef): Props = Props(new UserMasterActor(clientMessageDispatcher))
}
