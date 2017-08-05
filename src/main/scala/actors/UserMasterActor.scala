package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}

import scala.util.parsing.json.JSONObject

class UserMasterActor (val clientMessageDispatcher: ActorRef) extends UntypedAbstractActor{

  var databaseManager: ActorRef = _
  var loginManager: ActorRef = _
  var registrationManager: ActorRef = _

  override def preStart(): Unit = {

    databaseManager = context.actorOf(Props[DatabaseManagerActor] , "databaseManager")
    loginManager = context.actorOf(Props[LoginManagerActor], "loginManager")
    registrationManager = context.actorOf(Props[RegistrationManagerActor] , "registrationManager")

    super.preStart()
  }

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "newUser" => registrationManager ! message

    case "login" => loginManager ! message

    case "allMatchResult" => databaseManager ! message

    ///////// LOCAL MESSAGE HANDLER ////////////////

    case "registrationResult" => clientMessageDispatcher ! message

    case "loginResult" => {
      val res: String = message.asInstanceOf[JSONObject].obj("result").toString

      if (res == "success") {

        clientMessageDispatcher ! JSONObject(Map[String, String](
          "object" -> "newOnlinePlayer",
          "username" -> message.asInstanceOf[JSONObject].obj("username").toString,
          "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString        ))

        databaseManager ! JSONObject(Map[String, String](
          "object" -> "getPreviousMatchResult",
          "username" -> message.asInstanceOf[JSONObject].obj("username").toString,
          "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString))
      }

      //TODO: e se si dovesse sincronizzare dai messaggi
      else {
        clientMessageDispatcher ! JSONObject(Map[String, String](
          "object" -> "loginError",
          "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString))
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
