package actors

import actors.GameMasterActor._
import akka.actor.{Actor, ActorRef, Props}

/** Actor that manage the whole part related to game settings.
  * It dispatch message to his children actors depending on the type of message.
  *
  * @author manuBottax
  */
class GameMasterActor extends Actor {

  var characterManager: ActorRef = _
  var playgroundManager: ActorRef = _
  var friendManager: ActorRef = _
  var gameManager: ActorRef = _
  var endGameManager: ActorRef = _

  override def preStart(): Unit = {

    characterManager = context.actorOf(Props[CharacterManagerActor], "characterManager")
    playgroundManager = context.actorOf(Props[PlaygroundManagerActor], "playgroundManager")
    friendManager = context.actorOf(Props[FriendSearchManagerActor], "friendManager")
    gameManager = context.actorOf(Props[GameConfigurationManagerActor], "gameManager")
    endGameManager = context.actorOf(Props[GameEndManagerActor], "GameEndManager")

  }

  def receive = {
    case x: FriendMessage => {
      println("Searching for " + x.username + "  !")
      friendManager ! x
    }

    case CharacterMessage => {
      println(" character selection message !")
      characterManager ! CharacterMessage
    }
    case PlaygroundMessage => {
      println(" playground selection message !")
      playgroundManager ! PlaygroundMessage
    }
    case GameMessage => {
      println(" Game search message !")
      gameManager ! GameMessage
    }

    case x: GameEndMessage => {
      println("Game end message !")
      endGameManager ! x
    }

    //TODO Gestire le risposte

    case _  => println ("received unknown message")
  }


}

object GameMasterActor {
  case class FriendMessage (username: String)
  case object CharacterMessage
  case object PlaygroundMessage
  case object GameMessage
  case class GameEndMessage (result: Int/*result: MatchResult*/)
}
