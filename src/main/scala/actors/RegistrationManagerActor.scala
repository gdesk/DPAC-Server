package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}
import model.{ImmutableUser, User}
import scala.util.parsing.json.JSONObject

/** Actor that manage the registration of a new user user.
  * @author manuBottax
  */
class RegistrationManagerActor extends UntypedAbstractActor {

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "newUser" => {

      println("A new user want to register !")
      val name: String = message.asInstanceOf[JSONObject].obj("name").toString
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val email: String = message.asInstanceOf[JSONObject].obj("email").toString
      val password: String = message.asInstanceOf[JSONObject].obj("password").toString

      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      if (checkUsername(username)) {
        val newUser = new ImmutableUser(name, username, password, email)
        addUserToDB(newUser)
        println("Registration is completed successfully")
        sender() ! JSONObject( Map[String, String](
                                  "object" -> "registrationResult",
                                  "result" -> "success",
                                  "senderIp" -> ip )) //todo: l'ip andrà letto dal json in arrivo
      }

      else {
        println("Error during registration")
        sender() ! JSONObject(Map[String, String](
                                  "object" -> "registrationResult",
                                  "result" -> "fail",
                                  "senderIp" -> ip )) //todo: l'ip andrà letto dal json in arrivo
      }
    }

      //todo: solo lo username deve essere univoco e controllato ?

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  //todo -> come per messaggio di login, gira messaggio al database manager e aspetta la risposta (???)
  private def checkUsername(username: String): Boolean = true

  //todo
  private def addUserToDB(user: User): Unit = {}

}
