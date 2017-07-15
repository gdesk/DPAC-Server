package actors

import actors.LoginManagerActor.{LoginError, SuccessfulLogin}
import actors.UserMasterActor.LoginMessage
import akka.actor.Actor

/** Actor that manage the login of a user to the system.
  *
  * @author manuBottax
  */
class LoginManagerActor extends Actor {


  override def preStart(): Unit = {

    //todo: init()

  }

  def receive = {

    case LoginMessage (username, hashedPassword)=> {
      if ( checkUsername(username) && checkPassword(hashedPassword)){
        println(s"The user $username successfully log in !")
        sender() ! SuccessfulLogin
      }
      else
        sender() ! LoginError
    }

    case _  => println ("received unknown message")
  }

  //todo
  private def checkUsername(username: String): Boolean = true

  //todo
  private def checkPassword(password: String): Boolean = true

}

object LoginManagerActor {

  case object SuccessfulLogin
  case object LoginError

}

