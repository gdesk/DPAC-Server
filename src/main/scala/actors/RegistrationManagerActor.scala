package actors

import actors.RegistrationManagerActor.SuccessfulRegistration
import actors.UserMasterActor.RegistrationMessage
import akka.actor.{Actor, ActorRef, Props}

/** Actor that manage the registration of a new user user.
  * @author manuBottax
  */
class RegistrationManagerActor extends Actor {


  override def preStart(): Unit = {

    //todo: init()

  }

  def receive = {

    case RegistrationMessage (username, hashedPassword)=> {
      if ( checkUsername(username) ){
        //todo
        //addUserToDB(new UserImpl(username,hashedPassword)
      }
      println(s"The user $username has been registered !")
      sender() ! SuccessfulRegistration
    }

    case _  => println ("received unknown message")
  }

  //todo
  private def checkUsername(username: String): Boolean = true

  //todo
  private def addUserToDB(user: User): Unit = {}

}

object RegistrationManagerActor {

  case object SuccessfulRegistration

}
