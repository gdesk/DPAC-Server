package actors

import actors.GameMasterActor.GameMessage
import akka.actor.Actor

/** Actor that manage the creation of a match.
  *
  *  @author manuBottax
  */
class GameManagerActor extends Actor {


  override def preStart(): Unit = {

    //todo: init()

  }

  def receive = {

    case GameMessage => println(s"new Game message !")

    case _  => println ("received unknown message")
  }

}

object GameManagerActor {

  // todo: messaggi di risposta

}

