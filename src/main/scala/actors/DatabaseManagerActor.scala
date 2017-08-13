package actors

import akka.actor.UntypedAbstractActor
import database.DatabaseQuery
import model.{MatchResult, MatchResultImpl, User}

import scala.util.parsing.json.JSONObject

/** Actor that manage the database with user information.
  *
  * @author manuBottax
  */
class DatabaseManagerActor extends UntypedAbstractActor {

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

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
      val resultList: Option[List[Map[String, Any]]] = getMatchResultFor(username)

      println(resultList)

      context.parent ! JSONObject(Map[String, Any](
                       "object" -> "previousMatchResult",
                       "list" -> resultList,
                       "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString))
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
      addResult(user.username, new MatchResultImpl())
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  //todo: da testare
  private def getMatchResultFor(username: String): Option[List[Map[String, Any]]] = {
    val results: List[MatchResult] = DatabaseQuery.allMatches(username)
    var list: List[Map[String, Any]] = List()

    results.foreach((x) => {
      val result: Map[String, Any] = Map("result" -> x.result ,"score" -> x.score, "date" -> x.date)
      list = list ::: List(result)
    })

      Option(list)
  }

  // todo: da testare
  private def addUserToDB(user: User): Boolean = {
    DatabaseQuery addUser(user.name,user.username,user.mail,user.password)
  }

  //Todo: da testare
  private def checkLoginInfo(username: String, password: String): Boolean = {
    val result: String = DatabaseQuery checkLogin(username, password)

    result match {
      case "logged" => true
      case "passwordWrong" => false
      case "unregisteredUsername" => false
    }
  }

  //todo: da testare
  private def addResult(username: String, result: MatchResult): Boolean = {
    DatabaseQuery addMatchResult(username,result)
  }

}
