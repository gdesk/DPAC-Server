package actors

import java.awt.image.BufferedImage
import java.io.File
import java.util.Calendar

import akka.actor.{ActorRef, ActorSelection, Props, UntypedAbstractActor}
import model._

import scala.util.parsing.json.JSONObject

/** Actor that receive message from the server's actors, and send it trough the net to the addressee client.
  *
  * @author manuBottax
  */
class MessageDispatcherActor extends UntypedAbstractActor {

  var onlineClient: List[Client] = List()
  var onlineMatch: List[Match] = List()

  var pendingFriendRequest: Map[String, String] = Map()

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "addOnlinePlayer" => {
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString


      onlineClient = onlineClient ::: List (new ClientImpl(ip, username))
    }

    case "registrationResult" => {

      val result: String = message.asInstanceOf[JSONObject].obj("result").toString
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("registration has " + result)

      if (result == "success"){
        val reply = JSONObject(Map[String, Any](
          "object" -> "registrationResult",
          "result" -> true ))

        sendRemoteMessage(ip, reply)
      }

      else {
        val reply = JSONObject(Map[String, Any](
          "object" -> "registrationResult",
          "result" -> false ))

        sendRemoteMessage(ip, reply)
      }
    }

    case "loginError" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString

      println("Login has failed ")

      val reply: JSONObject = JSONObject(Map[String, Any](
        "object" -> "matches",
        "list" -> Option.empty[List[MatchResult]]))

      sendRemoteMessage(ip, reply)
    }

    case "previousMatchResult" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("Login ok ! Previous result loaded !")

      val res: List[Map[String, Any]] = List(Map ("result" -> true,"score" -> 42, "date" -> Calendar.getInstance()))

      val reply: JSONObject = JSONObject(Map[String, Any](
        "object" -> "matches",
        "list" ->  message.asInstanceOf[JSONObject].obj("list"))) //res )) //

      sendRemoteMessage(ip, reply)
    }

    case "logout" => {

      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      val user: Option[Client] = getClient(username)

      if (user.isDefined) {
        println(s"$username has logged out")

        val reply: JSONObject = JSONObject(Map[String, Any](
          "object" -> "logoutResponse",
          "response" -> true))

        sendRemoteMessage(ip, reply)
      }

      else {
        System.err.println(s"Error: $username not logged to this server, cannot log out.")

        val reply: JSONObject = JSONObject(Map[String, Any](
          "object" -> "logoutResponse",
          "response" -> false))

        sendRemoteMessage(ip, reply)
      }
    }

    case "ranges" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("sending available ranges")

      val reply: JSONObject = JSONObject(Map[String, Any](
        "object" -> "ranges",
        "list" -> message.asInstanceOf[JSONObject].obj("list")))

      sendRemoteMessage(ip, reply)
    }

    case "newPlayerInMatch" => {

      val currentMatch: Match = message.asInstanceOf[JSONObject].obj("match").asInstanceOf[Match]

      onlineMatch = onlineMatch ::: List(currentMatch)
    }

    case "addFriend" => {
      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val senderUsername: String = message.asInstanceOf[JSONObject].obj("senderUsername").toString

      println(s"$senderUsername ask $username for a match !")

      val friend: Option[Client] = getClient(username)

      if (friend.isDefined) {
        val friendIP: String = friend.get.ip
        pendingFriendRequest += (friendIP -> senderIP)
        val request: JSONObject = JSONObject(Map[String, Any](
          "object" -> "friendRequest",
          "senderRequest" -> senderUsername,
          "senderIP" -> senderIP))

        sendRemoteMessage(friendIP, request)
      }

        // other client is not logged to this server (is offline)
      else {
        val request: JSONObject = JSONObject(Map[String, Any](
          "object" -> "friendResponse",
          "responseRequest" -> false,
          "motivation" -> "offline"))

        sendRemoteMessage(senderIP, request)
      }
    }

    case "responseFriend" => {

      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val response: Boolean = message.asInstanceOf[JSONObject].obj("response").asInstanceOf[Boolean]

      val friendIP: String = pendingFriendRequest(senderIP)

      if (response) {
        val currentMatch: Match = getMatchFor(friendIP).get
        currentMatch.addPlayer(senderIP)

        context.actorSelection("user/matchMaster/gameConfigurationManager") ! JSONObject(Map[String, Any](
          "object" -> "updateMatch",
          "match" -> currentMatch ))

        val reply: JSONObject = JSONObject(Map[String, Any](
          "object" -> "friendResponse",
          "responseRequest" -> response,
          "motivation" -> "accepted"))

        sendRemoteMessage(friendIP, reply)
      }

      else {
        val reply: JSONObject = JSONObject(Map[String, Any](
          "object" -> "friendResponse",
          "responseRequest" -> response,
          "motivation" -> "refused"))

        sendRemoteMessage(friendIP, reply)
      }

      pendingFriendRequest -= senderIP
    }

    case "characterToChoose" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("sending available characters")

      sendRemoteMessage(ip, message)
    }

    case "availableCharacter" => {
      val available: Boolean = message.asInstanceOf[JSONObject].obj("available").asInstanceOf[Boolean]
      //val character: Map[String,Array[Byte]] = message.asInstanceOf[JSONObject].obj("map").asInstanceOf[Map[String,Array[Byte]]]
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("sending: character is available: " + available)

      sendRemoteMessage(ip, message)
    }

    case "notifySelection" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      notifyOtherClient(ip, message)
    }

    case "AvailablePlaygrounds" => {

      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("sending available playgrounds")


      val reply: JSONObject = JSONObject(Map[String, Any](
        "object" -> "playgrounds",
        "list" -> message.asInstanceOf[JSONObject].obj("list")))

      sendRemoteMessage(ip, reply)

    }

    case "playgroundChosen" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      broadcastMessage(ip, message.asInstanceOf[JSONObject])
    }

    case "characterChosen" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("sending character list for this match")

      sendRemoteMessage(ip, message)
    }

    case "otherPlayerIP" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val playerList: List[String] = message.asInstanceOf[JSONObject].obj("playerList").asInstanceOf[List[String]]

      //todo: posso configurare i giocatori per partita in questo punto
      println("sending other player IPs")

      sendConfigurationMessage(ip, message)
    }

    /*case "resultSaved" =>  {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("result saved in DB")

      sendRemoteMessage(ip, message)
    } */

      ///// client bootstrap ///////////////////
    case "clientCanConnect" => {

      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      val reply: JSONObject = JSONObject(Map[String, Any](
        "object" -> "ClientCanStartRunning"))

      broadcastConfigurationMessage(ip, reply)
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }



  private def sendRemoteMessage(ipAddress: String, message: Any): Unit = {

    val msgType: String = ActorsUtils.messageType(message)

    println("Send message [ " + msgType + " ] to : " + ipAddress)

    //test locale
    //val clientActorName = "fakeReceiver"
    //val receiver: ActorSelection = context.actorSelection("akka.tcp://DpacServer@" + ipAddress + ":4552" + "/user/" + clientActorName)

    //test con client
    val clientActorName = "fromServerCommunication"
    val receiver: ActorSelection = context.actorSelection("akka.tcp://DpacClient@" + ipAddress + ":2554" + "/user/" + clientActorName)


    receiver ! message
  }

  private def sendConfigurationMessage(ipAddress: String, message: Any): Unit = {

    println("Send Configuration message to : " + ipAddress)

    val receiver: ActorSelection = context.actorSelection("akka.tcp://DpacClient@" + ipAddress + ":2554" + "/user/P2PCommunication")
    receiver ! message
  }

  //todo: da testare
  private def sendNotificationMessage(ipAddress: String, message: Any): Unit = {

    println("Send notification message to : " + ipAddress)

    val receiver: ActorSelection = context.actorSelection("akka.tcp://DpacClient@" + ipAddress + ":2554" + "/user/FromServerCommunication")
    receiver ! message
  }

  //todo: da testare
  private def broadcastMessage(ip: String, message: JSONObject): Unit = {

    val ipList: List[String] = getMatchFor(ip).get.involvedPlayer

    if (ipList.nonEmpty){
      println("broadcast message to " + ipList.size + " client")
      ipList.foreach((x) => sendRemoteMessage(x,message))
    }


  }

  //todo: da testare
  private def broadcastConfigurationMessage(ip: String, message: JSONObject): Unit = {
    val ipList: List[String] = getMatchFor(ip).get.involvedPlayer

    if (ipList.nonEmpty){
      println("broadcast message to " + ipList.size + " client")
      ipList.foreach((x) => sendConfigurationMessage(x,message))
    }
  }


  //todo: da testare
  private def notifyOtherClient(excludedClient: String, message: Any): Unit =  {
    val ipList: List[String] = getMatchFor(excludedClient).get.involvedPlayer

    if (ipList.nonEmpty){
      println("sending notification to " + ipList.size + " clients")
      ipList.foreach((x) => if (x != excludedClient) sendNotificationMessage(x,message))
    }
  }


  private def getClient(username: String): Option[Client] =  {
    onlineClient.find((x) => x.username == username)
  }

  private def getMatchFor(ip: String): Option[Match] = {
    onlineMatch.find((x) => x.involvedPlayer.contains(ip))
  }


}