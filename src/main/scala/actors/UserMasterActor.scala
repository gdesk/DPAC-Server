package actors

import actors.UserMasterActor.{LoginMessage, RegistrationMessage, UserMessage}
import akka.actor.{Actor, ActorRef, Props}

/** Actor that manage the whole part related to user.
  * It dispatch message to his children actors depending on the type of message.
  *
  * @author manuBottax
  */
class UserMasterActor extends Actor {

  var userManager: ActorRef = _
  var loginManager: ActorRef = _
  var registrationManager: ActorRef = _

  override def preStart(): Unit = {

    userManager = context.actorOf(Props[UserManagerActor], "userManager")

    loginManager = context.actorOf(Props[LoginManagerActor], "loginManager")

    registrationManager = context.actorOf(Props[RegistrationManagerActor], "registrationManager")

  }

  def receive = {
    case x: RegistrationMessage => {
      println("The user " + x.username + " want to register !")
      registrationManager ! x
    }

    case x: LoginMessage => {
      println("The user " + x.username + " want to login !")
      loginManager ! x
    }

    case UserMessage => {
      println("The user send a message !")
      userManager ! UserMessage
    }

      //TODO Gestire le risposte

    case _  => println ("received unknown message")
  }


}

object UserMasterActor {
  case class RegistrationMessage (username: String, hashedPassword: String)
  case class LoginMessage (username: String, hashedPassword: String)
  case object UserMessage
}
