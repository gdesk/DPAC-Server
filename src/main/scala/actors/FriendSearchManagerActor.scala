package actors

import akka.actor.{Actor, UntypedAbstractActor}

/** Actor that manage the research of friends for a private match.
  *
  *  @author manuBottax
  */
class FriendSearchManagerActor extends UntypedAbstractActor {


  override def preStart(): Unit = {

    //todo: init()

  }

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    //case FriendMessage => println(s"new Friend message !")

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

}

object FriendSearchManagerActor {

  // todo: messaggi di risposta

}
