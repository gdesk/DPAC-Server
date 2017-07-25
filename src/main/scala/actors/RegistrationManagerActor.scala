package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}
import model.{ImmutableUser, User}
import scala.util.parsing.json.JSONObject

/** Actor that manage the registration of a new user user.
  * @author manuBottax
  */
class RegistrationManagerActor (val clientMessageDispatcher: ActorRef) extends UntypedAbstractActor {

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
        clientMessageDispatcher ! JSONObject( Map[String, String](
                                  "object" -> "registrationResult",
                                  "result" -> "success",
                                  "senderIp" -> ip )) //todo: l'ip andrà letto dal json in arrivo
      }

      else {
        println("Error during registration")
        clientMessageDispatcher ! JSONObject(Map[String, String](
                                  "object" -> "registrationResult",
                                  "result" -> "fail",
                                  "senderIp" -> ip )) //todo: l'ip andrà letto dal json in arrivo
      }
    }

      //todo: solo lo username deve essere univoco e controllato ?

    case _  => println ("received unknown message")
  }

  //todo -> come per messaggio di login, gira messaggio al database manager e aspetta la risposta (???)
  private def checkUsername(username: String): Boolean = true

  //todo
  private def addUserToDB(user: User): Unit = {}

}

object RegistrationManagerActor {

    /**
      * Create Props for an actor of this type.
      *
      * @param clientMessageDispatcher the reference to the actor that send message to the client
      * @return a Props for creating this actor.
      */
    def props(clientMessageDispatcher: ActorRef): Props = Props(new RegistrationManagerActor(clientMessageDispatcher))
}
