package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}
import utils.ActorsUtils

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

    case "rangesRequest" => gameManager ! message //ok

    case "selectedRange" => gameManager ! message //ok

    case "addFriend" => clientMessageDispatcher ! message

    case "responseFriend" => clientMessageDispatcher ! message

    case "characterToChooseRequest" => characterManager ! message //ok

    case "chooseCharacter" => characterManager ! message  //ok

    case "teamCharacterRequest" => characterManager ! message

    case "playgrounds" => playgroundManager ! message //da completare

    case "chosenPlayground" => playgroundManager ! message  //ok

    case "matchResult" => endGameManager ! message  //ok

    /// PeerBootstrap
    case "startGame" => gameManager ! message //ok
    ///// PeerBootstrap
    case "serverIsRunning" => gameManager ! message //ok


    //case "startGame" => peerBootstrapManager ! message

    ////// LOCAL MESSAGE HANDLER ////////////////////


    case "ranges" => clientMessageDispatcher ! message  //ok

    case "newPlayerInMatch" => clientMessageDispatcher ! message

    case "characterToChoose" => clientMessageDispatcher ! message //ok

    case "availableCharacter" => clientMessageDispatcher ! message  //ok

    case "notifySelection" => clientMessageDispatcher ! message //ok

    case "characterChosen" => clientMessageDispatcher ! message //

    case "AvailablePlaygrounds" => clientMessageDispatcher ! message  //ok

    case "playgroundChosen" => clientMessageDispatcher ! message  //ok

    case "otherPlayerIP" => clientMessageDispatcher ! message //ok

    // case "resultSaved" => clientMessageDispatcher ! message

    case "clientCanConnect" => clientMessageDispatcher ! message  //ok

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
