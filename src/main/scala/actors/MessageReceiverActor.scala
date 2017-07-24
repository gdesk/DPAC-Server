package actors

import akka.actor.{Actor, ActorRef, Props, UntypedAbstractActor}

import scala.util.parsing.json.JSONObject

/** Actor that receive message from the net, parse it and send appropriate message to the actor in the server.
  *
  * @author manuBottax
  */
class MessageReceiverActor (val clientMessageDispatcher: ActorRef) extends UntypedAbstractActor {

  var databaseManager: ActorRef = _
  var loginManager: ActorRef = _
  var registrationManager: ActorRef = _

  var characterManager: ActorRef = _
  var playgroundManager: ActorRef = _
  var friendManager: ActorRef = _
  var gameManager: ActorRef = _
  var endGameManager: ActorRef = _

  override def preStart(): Unit = {

    databaseManager = context.actorOf(DatabaseManagerActor.props(clientMessageDispatcher))
    loginManager = context.actorOf(LoginManagerActor.props(clientMessageDispatcher))
    registrationManager = context.actorOf(RegistrationManagerActor.props(clientMessageDispatcher))

    characterManager = context.actorOf(Props[CharacterManagerActor], "characterManager")
    playgroundManager = context.actorOf(Props[PlaygroundManagerActor], "playgroundManager")
    friendManager = context.actorOf(Props[FriendSearchManagerActor], "friendManager")
    gameManager = context.actorOf(Props[GameConfigurationManagerActor], "gameManager")
    endGameManager = context.actorOf(Props[GameEndManagerActor], "GameEndManager")

  }

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "newUser" => registrationManager ! message

    case "login" => loginManager ! message

    /*case x: LoginMessage => {
      println("The user " + x.username + " want to login !")
      loginManager ! x
    }

    case UserMessage => {
      println("The user send a message !")
      userManager ! UserMessage
    }*/

    //TODO Gestire le risposte

      ///// LOCAL MESSAGE HANDLER ////////////////

    case "loginResult" =>{
      val res: String = message.asInstanceOf[JSONObject].obj("result").toString

      if (res == "success") {
        databaseManager ! JSONObject(Map[String, String](
                      "object" -> "getPreviousMatchResult",
                      "username" -> message.asInstanceOf[JSONObject].obj("username").toString,
                      "senderIp" -> message.asInstanceOf[JSONObject].obj("senderIp").toString))
      }
      else {
          clientMessageDispatcher ! JSONObject(Map[String, String](
                                    "object" -> "loginError",
                                    "senderIp" -> message.asInstanceOf[JSONObject].obj("senderIp").toString))
        }
    }

    case _  => println ("received unknown message")
  }


}

object MessageReceiverActor {

  /**
    * Create Props for an actor of this type.
    *
    * @param clientMessageDispatcher the reference to the actor that send message to the client
    * @return a Props for creating this actor.
    */
  def props(clientMessageDispatcher: ActorRef): Props = Props(new MessageReceiverActor(clientMessageDispatcher))
}
