package actors

import actors.UserMasterActor.UserMessage
import akka.actor.Actor

/** Actor that manage the user on a System.
  *
  * @author manuBottax
  */
class UserManagerActor extends Actor {

  //todo: questo attore interagisce con il db e mi restituisce informazioni sugli utenti => probabilmente servirà un parametro


  override def preStart(): Unit = {

    //todo: init()

  }

  def receive = {

    case UserMessage => println("An User Message is arrived")
    case _  => println ("received unknown message")
  }


}

object UserManagerActor {

// todo

}
