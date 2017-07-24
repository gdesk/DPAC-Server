package actors

import akka.actor.Actor

/** Actor that manage the choice of a playground for the match.
  *
  *  @author manuBottax
  */
class PlaygroundManagerActor extends Actor {


  override def preStart(): Unit = {

    //todo: init()

  }

  def receive = {

    //case PlaygroundMessage => println(s"new Playground message !")

    case _  => println ("received unknown message")
  }

}

object PlaygroundManagerActor {

  // todo: messaggi di risposta

}
