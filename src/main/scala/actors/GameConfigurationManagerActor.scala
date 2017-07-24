package actors

import akka.actor.Actor

/** Actor that manage the creation of a match.
  *
  *  @author manuBottax
  */
class GameConfigurationManagerActor extends Actor {


  override def preStart(): Unit = {

    //todo: init()

  }

  def receive = {

    //case GameMessage => println(s"new Game message !")

    case _  => println ("received unknown message")
  }

}

object GameConfigurationManagerActor {

  // todo: messaggi di risposta

}

