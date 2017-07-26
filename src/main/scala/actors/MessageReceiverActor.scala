package actors

import akka.actor.{Actor, ActorRef, Props, UntypedAbstractActor}
import model.MatchManager

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

    databaseManager = context.actorOf(DatabaseManagerActor.props(clientMessageDispatcher) , "databaseManager")
    loginManager = context.actorOf(Props[LoginManagerActor], "loginManager")
    registrationManager = context.actorOf(RegistrationManagerActor.props(clientMessageDispatcher) , "registrationManager")

    characterManager = context.actorOf(Props[CharacterManagerActor], "characterManager")
    playgroundManager = context.actorOf(Props[PlaygroundManagerActor], "playgroundManager")
    friendManager = context.actorOf(Props[FriendSearchManagerActor], "friendManager")
    gameManager = context.actorOf(GameConfigurationManagerActor.props(new MatchManager), "gameConfigurationManager")
    endGameManager = context.actorOf(Props[GameEndManagerActor], "gameEndManager")


  }

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "newUser" => registrationManager ! message

    case "login" => loginManager ! message

    case "rangesRequest" => gameManager ! message

    case "selectedRange" => gameManager ! message

    case "startGame" => gameManager ! message

    case "characterToChooseRequest" => characterManager ! message

    case "chooseCharacter" => characterManager ! message

    case "playgrounds" => playgroundManager ! message

    case "chosenPlayground" => playgroundManager ! message

    case "matchResult" => endGameManager ! message

    case "allMatchResult" => databaseManager ! message

    //TODO Gestire le risposte

    ///////// LOCAL MESSAGE HANDLER ////////////////

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

    case "ranges" => clientMessageDispatcher ! message

    case "characterToChoose" => clientMessageDispatcher ! message

    case "availableCharacter" => clientMessageDispatcher ! message

    case "notifySelection" => clientMessageDispatcher ! message

    case "AvailablePlaygrounds" => clientMessageDispatcher ! message

    case "playgroundChosen" => clientMessageDispatcher ! message

    case "otherPlayerIP" => clientMessageDispatcher ! message

    case "resultSaved" => clientMessageDispatcher ! message

    case _ => println("received unknown message")
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
