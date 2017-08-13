package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}
import model.{ImmutableUser, User}
import utils.ActorsUtils

import scala.util.parsing.json.JSONObject

/** Actor that manage the registration of a new user user.
  * @author manuBottax
  */
class RegistrationManagerActor extends UntypedAbstractActor {

  private var pendingUser: List[User] = List()

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "newUser" => {

      println("A new user want to register !")

      val name: String = message.asInstanceOf[JSONObject].obj("name").toString
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val email: String = message.asInstanceOf[JSONObject].obj("email").toString
      val password: String = message.asInstanceOf[JSONObject].obj("password").toString
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString


      val user: User = new ImmutableUser(name, username, password, email)

      pendingUser = pendingUser ::: List(user)

      context.actorSelection("../databaseManager") ! JSONObject(Map[String, Any](
        "object" -> "addUserToDB",
        "user" -> user,
        "senderIP" -> ip ))
    }


    case "registrationResult" => {

      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val result: String = message.asInstanceOf[JSONObject].obj("result").toString


      val currentUser: Option[User] = pendingUser.find((x) => x.username == username)

      if (currentUser.isDefined) {

        pendingUser = pendingUser.filter( _ != currentUser.get)
        result match {

          case "success" => {
            println("Registration is completed successfully")
            context.parent ! JSONObject(Map[String, String](
              "object" -> "registrationResult",
              "result" -> "success",
              "senderIP" -> ip))
          }

          case _ => {
            println("Error during registration")
            context.parent ! JSONObject(Map[String, String](
              "object" -> "registrationResult",
              "result" -> "fail",
              "senderIP" -> ip))

          }
        }
      }
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

}
