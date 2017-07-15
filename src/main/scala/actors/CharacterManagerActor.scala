package actors

import actors.GameMasterActor.{CharacterMessage, PlaygroundMessage}
import akka.actor.Actor

/** Actor that manage the choice of a character for the match.
  *
  *  @author manuBottax
  */
class CharacterManagerActor extends Actor {


  override def preStart(): Unit = {

    //todo: init()

  }

  def receive = {

    case CharacterMessage => println(s"new Character message !")

    case _  => println ("received unknown message")
  }

}

object CharacterManagerActor {

  // todo: messaggi di risposta

}
