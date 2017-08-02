package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}

class MatchMasterActor (val clientMessageDispatcher: ActorRef) extends UntypedAbstractActor {

  var characterManager: ActorRef = _
  var playgroundManager: ActorRef = _
  var friendManager: ActorRef = _
  var gameManager: ActorRef = _
  var endGameManager: ActorRef = _
  var peerBootstrapManager: ActorRef = _

  override def preStart(): Unit = {

    characterManager = context.actorOf(Props[CharacterManagerActor], "characterManager")
    playgroundManager = context.actorOf(Props[PlaygroundManagerActor], "playgroundManager")
    friendManager = context.actorOf(Props[FriendSearchManagerActor], "friendManager")
    gameManager = context.actorOf(Props[GameConfigurationManagerActor], "gameConfigurationManager")
    endGameManager = context.actorOf(Props[GameEndManagerActor], "gameEndManager")
    peerBootstrapManager = context.actorOf(Props[peerBootstrapManagerActor], "PeerBootstrapdManager")

  }

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "rangesRequest" => gameManager ! message

    case "selectedRange" => gameManager ! message

    case "startGame" => gameManager ! message

    case "characterToChooseRequest" => characterManager ! message

    case "chooseCharacter" => characterManager ! message

    case "playgrounds" => playgroundManager ! message

    case "chosenPlayground" => playgroundManager ! message

    case "matchResult" => endGameManager ! message

    //todo: Se fosse una stringa sarebbe meglio
    case "serverIsRunning" => peerBootstrapManager ! message

    ////// LOCAL MESSAGE HANDLER ////////////////////


    case "ranges" => clientMessageDispatcher ! message

    case "characterToChoose" => clientMessageDispatcher ! message

    case "availableCharacter" => clientMessageDispatcher ! message

    case "notifySelection" => clientMessageDispatcher ! message

    case "AvailablePlaygrounds" => clientMessageDispatcher ! message

    case "playgroundChosen" => clientMessageDispatcher ! message

    case "otherPlayerIP" => clientMessageDispatcher ! message

    case "resultSaved" => clientMessageDispatcher ! message

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
