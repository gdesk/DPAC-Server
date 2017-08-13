package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}
import utils.ActorsUtils

/** An actor that handle the match-related message and dispatch it to the various actor.
  * It also initialize the match-related actors.
  *
  * @author manuBottax
  */
class MatchMasterActor (val clientMessageDispatcher: ActorRef) extends UntypedAbstractActor {

  var characterManager: ActorRef = _
  var playgroundManager: ActorRef = _
  var gameManager: ActorRef = _
  var endGameManager: ActorRef = _

  override def preStart(): Unit = {

    characterManager = context.actorOf(Props[CharacterManagerActor], "characterManager")
    playgroundManager = context.actorOf(Props[PlaygroundManagerActor], "playgroundManager")
    gameManager = context.actorOf(Props[GameConfigurationManagerActor], "gameConfigurationManager")
    endGameManager = context.actorOf(Props[GameEndManagerActor], "gameEndManager")
  }

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "rangesRequest" => gameManager ! message

    case "selectedRange" => gameManager ! message

    case "addFriend" => clientMessageDispatcher ! message

    case "responseFriend" => clientMessageDispatcher ! message

    case "characterToChooseRequest" => characterManager ! message

    case "chooseCharacter" => characterManager ! message

    case "teamCharacterRequest" => characterManager ! message

    case "playgrounds" => playgroundManager ! message

    case "chosenPlayground" => playgroundManager ! message

    case "matchResult" => endGameManager ! message

    case "startGame" => gameManager ! message

    case "serverIsRunning" => gameManager ! message

    ////// LOCAL MESSAGE HANDLER ////////////////////

    case "ranges" => clientMessageDispatcher ! message

    case "newPlayerInMatch" => clientMessageDispatcher ! message

    case "characterToChoose" => clientMessageDispatcher ! message

    case "availableCharacter" => clientMessageDispatcher ! message

    case "notifySelection" => clientMessageDispatcher ! message

    case "characterChosen" => clientMessageDispatcher ! message

    case "AvailablePlaygrounds" => clientMessageDispatcher ! message

    case "playgroundChosen" => clientMessageDispatcher ! message

    case "otherPlayerIP" => clientMessageDispatcher ! message

    case "clientCanConnect" => clientMessageDispatcher ! message

    case "previousMatchResult" => clientMessageDispatcher ! message

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }
}

object MatchMasterActor {

  /**
    * Create Props for an actor of this type.
    *
    * @param clientMessageDispatcher the reference to the actor that send message to the client
    * @return a Props for creating this actor.
    */
  def props(clientMessageDispatcher: ActorRef): Props = Props(new MatchMasterActor(clientMessageDispatcher))

}
