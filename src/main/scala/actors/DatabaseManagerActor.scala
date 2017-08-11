package actors

import java.util.Calendar

import akka.actor.UntypedAbstractActor
import model.{MatchResult, MatchResultImpl, User}

import scala.util.parsing.json.JSONObject

/** Actor that manage the database with user information.
  *
  * @author manuBottax
  */
class DatabaseManagerActor extends UntypedAbstractActor {

  //todo: questo attore interagisce con il db e mi restituisce informazioni sugli utenti => probabilmente servirà un parametro

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "checkUsername" => {

      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString

      if (  checkAvailableUsername(username) ) {

        sender() ! JSONObject(Map[String, String](
          "object" -> "usernameCheckResult",
          "username" -> username,
          "result" -> "success",
          "senderIP" -> ip))
      }

      else {

        sender() ! JSONObject(Map[String, String](
          "object" -> "usernameCheckResult",
          "username" -> username,
          "result" -> "fail",
          "senderIP" -> ip))
      }
    }

    case "addUserToDB" => {

      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val user: User = message.asInstanceOf[JSONObject].obj("user").asInstanceOf[User]

      if( addUserToDB(user) ) {

        sender() ! JSONObject(Map[String, String](
          "object" -> "registrationResult",
          "username" -> user.username,
          "result" -> "success",
          "senderIP" -> ip))
      }

      else {

        sender() ! JSONObject(Map[String, String](
          "object" -> "registrationResult",
          "username" -> user.username,
          "result" -> "fail",
          "senderIP" -> ip))
      }

    }

    case "checkUsernameAndPassword" => {

      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val password: String = message.asInstanceOf[JSONObject].obj("password").toString

      if( checkLoginInfo(username, password) ) {

        sender() ! JSONObject(Map[String, String](
          "object" -> "checkLoginInfoResult",
          "result" -> "success",
          "username" -> username,
          "senderIP" -> ip))
      }

      else{

        sender() ! JSONObject(Map[String, String](
          "object" -> "checkLoginInfoResult",
          "result" -> "fail",
          "username" -> username,
          "senderIP" -> ip))
      }

    }


    case "getPreviousMatchResult" => {
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      //todo: dovrebbe essere una list
      val resultList: Option[List[Map[String, Any]]] = getMatchResultFor(username)

      // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      //Ho un'idea per gestire la sincronizzazione ! si fa una lista di pending come di la, quando arriva il messaggio si lancia il metodo
      // e il metodo stesso invia un messaggio a self quando ha finito, dicendo che è prnoto il risultato, e a quel punto viene gestito quel messaggio
      // e risponde a quello che ha ricevuto  il messaggio.

        context.parent ! JSONObject(Map[String, Any](
                                  "object" -> "previousMatchResult",
                                  "list" -> resultList,
                                  "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString  ))
    }

    case "allMatchResult" => self ! JSONObject(Map[String, Any](
          "object" -> "getPreviousMatchResult",
          "username" -> message.asInstanceOf[JSONObject].obj("username").toString,
          "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))

      //todo: gestire match result con il model del client
    case "addResult" => {
      val user: User = message.asInstanceOf[JSONObject].obj("user").asInstanceOf[User]
      val result: MatchResult = message.asInstanceOf[JSONObject].obj("result").asInstanceOf[MatchResult]

      println(s"add a new result to db: ($result) from $user ")
      addResult(user, new MatchResultImpl())
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  //todo
  private def getMatchResultFor(username: String): Option[List[Map[String, Any]]] = Option(List(Map ("result" -> true,"score" -> 42, "date" -> Calendar.getInstance())))

  //todo non serve perchè lo fa già in automatico con add
  private def checkAvailableUsername(username: String): Boolean = true

  //todo
  private def addUserToDB(user: User): Boolean = true

  //Todo
  private def checkLoginInfo(username: String, password: String): Boolean = true

  //todo
  private def addResult(user: User, result: MatchResult): Boolean = true

}
