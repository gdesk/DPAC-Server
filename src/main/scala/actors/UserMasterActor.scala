package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}
import model.{Client, ClientImpl}

import scala.util.parsing.json.JSONObject

class UserMasterActor (val clientMessageDispatcher: ActorRef) extends UntypedAbstractActor{

  var databaseManager: ActorRef = _
  var loginManager: ActorRef = _
  var registrationManager: ActorRef = _
  //var clientManager: ActorRef = _

  override def preStart(): Unit = {

    databaseManager = context.actorOf(Props[DatabaseManagerActor] , "databaseManager")
    loginManager = context.actorOf(Props[LoginManagerActor], "loginManager")
    registrationManager = context.actorOf(Props[RegistrationManagerActor] , "registrationManager")
    //clientManager = context.actorOf(Props[ClientManagerActor], "clientManager")

    super.preStart()
  }

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "newUser" => registrationManager ! message

    case "login" => loginManager ! message

    case "logout" => clientMessageDispatcher ! message

    case "allMatchResult" => databaseManager ! message

    ///////// LOCAL MESSAGE HANDLER ////////////////

    case "registrationResult" => clientMessageDispatcher ! message

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

      ////////// /////////////////////

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
