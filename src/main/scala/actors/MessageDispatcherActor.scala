package actors

import akka.actor.{ActorSelection, UntypedAbstractActor}
import model._
import utils.ActorsUtils

import scala.util.parsing.json.JSONObject

/** Actor that receive message from the server's actors, and send it trough the net to the addressee client.
  * It also handle the online client list, the current match and the pending friend request, waiting for the response.
  *
  * @author manuBottax
  */
class MessageDispatcherActor extends UntypedAbstractActor {

  var onlineClient: List[Client] = List()
  var onlineMatch: List[Match] = List()

  var pendingFriendRequest: Map[String, String] = Map()

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

      // the result of the registration procedure.
    case "registrationResult" => {
      val result: String = message.asInstanceOf[JSONObject].obj("result").toString
      //val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("registration has " + result)

      if (result == "success"){
        val reply = JSONObject(Map[String, Any](
          "object" -> "registrationResult",
          "result" -> true ))

        sendRemoteMessage(ActorsUtils.getSenderIP(message), reply)
      }

      else {
        val reply = JSONObject(Map[String, Any](
          "object" -> "registrationResult",
          "result" -> false ))

        sendRemoteMessage(ActorsUtils.getSenderIP(message), reply)
      }
    }

      // handler for the errors on the login procedure.
    case "loginError" => {
      //val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("Login has failed")

      val reply: JSONObject = JSONObject(Map[String, Any](
        "object" -> "matches",
        "list" -> None ))

      sendRemoteMessage(ActorsUtils.getSenderIP(message), reply)
    }

      // when login is ok the client receiver the list of his previous results.
      // results are send in a JSON-Style map on parameter 'list'.
    case "previousMatchResult" => {
      //val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("Login ok ! Previous result loaded !")

      val reply: JSONObject = JSONObject(Map[String, Any](
        "object" -> "matches",
        "list" ->  message.asInstanceOf[JSONObject].obj("list") ))

      sendRemoteMessage(ActorsUtils.getSenderIP(message), reply)
    }

      // handler for client logout procedure
    case "logout" => {
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      //val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      val user: Option[Client] = getClient(username)

      if (user.isDefined) {


        println("OnlineClient: " + onlineClient.size)

        print (s"$username has logged out - ")

        onlineClient = onlineClient.filterNot((x) => x.IPAddress == user.get.IPAddress)
        val currentMatch = getMatchFor(user.get.IPAddress)

        println ("now online: " + onlineClient.size)

        if (currentMatch isDefined) {

          onlineMatch = onlineMatch.filterNot((x) => x.involvedPlayerIP.contains(user.get.IPAddress))
          currentMatch.get.involvedPlayerIP = currentMatch.get.involvedPlayerIP.filterNot((x) => x == user.get.IPAddress)

          context.actorSelection("user/matchMaster/gameConfigurationManager") ! JSONObject(Map[String, Any](
            "object" -> "updateMatch",
            "match" -> currentMatch))
        }

        val reply: JSONObject = JSONObject(Map[String, Any](
          "object" -> "logoutResponse",
          "response" -> true))

        sendRemoteMessage(ActorsUtils.getSenderIP(message), reply)
      }

      else {
        System.err.println(s"Error: $username not logged to this server, cannot log out.")

        val reply: JSONObject = JSONObject(Map[String, Any](
          "object" -> "logoutResponse",
          "response" -> false))

        sendRemoteMessage(ActorsUtils.getSenderIP(message), reply)
      }
    }

      // send the available ranges to a client
    case "ranges" => {
      //val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("sending available ranges")

      val reply: JSONObject = JSONObject(Map[String, Any](
        "object" -> "ranges",
        "list" -> message.asInstanceOf[JSONObject].obj("list")))

      sendRemoteMessage(ActorsUtils.getSenderIP(message), reply)
    }

      // handler for a new player insertion in a match
    case "newPlayerInMatch" => {
      val selectedMatch: Match = message.asInstanceOf[JSONObject].obj("match").asInstanceOf[Match]


      if (! onlineMatch.contains(selectedMatch)) {
        onlineMatch =  List(selectedMatch) ::: onlineMatch
      }

      println("new player in match")

      //notify other client that the player has joined the match
      selectedMatch.involvedPlayerIP.foreach((x) => {
        val reply: JSONObject = JSONObject(Map[String, Any](
          "object" -> "playerInMatch",
          "players" -> selectedMatch.involvedPlayerIP.size ))
        sendNotificationMessage(x,reply)
      })
    }

    case "removePlayerFromMatch" => {
      val selectedMatch: Match = message.asInstanceOf[JSONObject].obj("match").asInstanceOf[Match]
      val senderIP: String = ActorsUtils.getSenderIP(message)

      println(s" $senderIP added to a new match, removing from the previous one ( n° " + selectedMatch.id + ")" )

      selectedMatch.removePlayer(senderIP)
    }


    // handler for friend request
    case "addFriend" => {
      val senderIP: String = ActorsUtils.getSenderIP(message)
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val senderUsername: String = message.asInstanceOf[JSONObject].obj("senderUsername").toString

      println(s"$senderUsername ask $username for a match !")

      val friend: Option[Client] = getClient(username)

      if (friend.isDefined) {
        val friendIP: String = friend.get.IPAddress

        println("Send friend request")

        // save temporary the couple in order to handle the response
        pendingFriendRequest += (friendIP -> senderIP)

        val request: JSONObject = JSONObject(Map[String, Any](
          "object" -> "friendRequest",
          "senderRequest" -> senderUsername,
          "senderIP" -> senderIP))

        sendRemoteMessage(friendIP, request)
      }

        // other client is not logged to this server (is offline)
      else {

        println(s"request not send. $username is offline.")

        val reply: JSONObject = JSONObject(Map[String, Any](
          "object" -> "friendResponse",
          "responseRequest" -> false,
          "motivation" -> "offline"))

        sendRemoteMessage(senderIP, reply)
      }
    }

      // handler for the response of a friend request
    case "responseFriend" => {
      val senderIP: String = ActorsUtils.getSenderIP(message)
      val response: Boolean = message.asInstanceOf[JSONObject].obj("response").asInstanceOf[Boolean]

      val friendIP: String = pendingFriendRequest(senderIP)

      if (response) {
        // add friend to the match
        val currentMatch: Match = getMatchFor(friendIP).get
        currentMatch.addPlayer(senderIP)

        println("friend request accepted ! :) ")

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
        println("friend request refused ! :( ")
        val reply: JSONObject = JSONObject(Map[String, Any](
          "object" -> "friendResponse",
          "responseRequest" -> response,
          "motivation" -> "refused"))

        sendRemoteMessage(friendIP, reply)
      }

      // remove couple reference because the request is completed
      pendingFriendRequest -= senderIP
    }

      // send the list of the available character
    case "characterToChoose" => {
      //val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("sending available characters")

      sendRemoteMessage(ActorsUtils.getSenderIP(message), message)
    }

      // send the response to the availability request for a character
    case "availableCharacter" => {
      val available: Boolean = message.asInstanceOf[JSONObject].obj("available").asInstanceOf[Boolean]
      //val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("character is available: " + available)

      sendRemoteMessage(ActorsUtils.getSenderIP(message), message)
    }

      // notify other client in the match the selection of a character
    case "notifySelection" => {
      //val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      notifyOtherClient(ActorsUtils.getSenderIP(message), message)
    }

      // send to a client the data for the other character chosen for the match by the other players.
    case "characterChosen" => {
      //val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("sending character list for this match")

      sendRemoteMessage(ActorsUtils.getSenderIP(message), message)
    }

      // send the list of the available playground
    case "AvailablePlaygrounds" => {
      //val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("sending available playgrounds")

      val reply: JSONObject = JSONObject(Map[String, Any](
        "object" -> "playgrounds",
        "list" -> message.asInstanceOf[JSONObject].obj("list")))

      sendRemoteMessage(ActorsUtils.getSenderIP(message), reply)
    }

      // send the chosen playground, result of the votation, to all the client for the match
    case "playgroundChosen" => {
      //val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("sending chosen playground.")
      broadcastMessage(ActorsUtils.getSenderIP(message), message.asInstanceOf[JSONObject])
    }

      //send to a client the list of other client for the selected match
    case "otherPlayerIP" => {
      //val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("sending other player IPs")

      sendConfigurationMessage(ActorsUtils.getSenderIP(message), message)
    }

      ///// client bootstrap /////
      // send a notification to the other client that all the servers in the P2P net are ready and the match can start.
    case "clientCanConnect" => {
      //val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("Servers ready. Clients can connect.")

      val reply: JSONObject = JSONObject(Map[String, Any](
        "object" -> "ClientCanStartRunning"))

      broadcastConfigurationMessage(ActorsUtils.getSenderIP(message), reply)
    }

      ///// LOCAL UTILITY HANDLER /////
      // add a new player to the list of the online client, received when the login is successful
    case "addOnlinePlayer" => {
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val ip: String = ActorsUtils.getSenderIP(message)

      println(s"New online client ($username from $ip).")
      onlineClient = onlineClient ::: List (new ClientImpl(ip, username))
    }

      // get the number of client connected to a match.
    case "getMatchSize" => {
      val senderIP: String = ActorsUtils.getSenderIP(message)
      val currentMatch: Option[Match] = getMatchFor(senderIP)

      println("requested match size for match n° " + currentMatch.get.id)

      if(currentMatch.isDefined){
        sender() ! JSONObject(Map[String, Any](
          "object" -> "matchSize",
          "matchSize" -> currentMatch.get.involvedPlayerIP.size,
          "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))
      }
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }


  /** Send a message to a remote host.
    *
    * @param ipAddress: the host IP address
    * @param message: the message to be sent
    */
  private def sendRemoteMessage(ipAddress: String, message: Any): Unit = {

    val msgType: String = ActorsUtils.messageType(message)

    println("Send message [ " + msgType + " ] to : " + ipAddress)

    //test locale
    //val clientActorName = "fakeReceiver"
    //val receiver: ActorSelection = context.actorSelection("akka.tcp://DpacServer@" + ipAddress + ":4552" + "/user/" + clientActorName)

    // this message is received from the main client communication actor
    val clientActorName = "fromServerCommunication"
    val receiver: ActorSelection = context.actorSelection("akka.tcp://DpacClient@" + ipAddress + ":2554" + "/user/" + clientActorName)

    receiver ! message
  }

  /** Send a configuration message to a remote host.
    *
    * @param ipAddress: the host IP address
    * @param message: the message to be sent
    */
  private def sendConfigurationMessage(ipAddress: String, message: Any): Unit = {

    val msgType: String = ActorsUtils.messageType(message)

    println("Send Configuration message [ " + msgType + " ] to : " + ipAddress)

    // this message is received from the P2P configuration actor
    val receiver: ActorSelection = context.actorSelection("akka.tcp://DpacClient@" + ipAddress + ":2554" + "/user/P2PCommunication")
    receiver ! message
  }

  //todo: da testare
  /** Send a notification message to a remote host.
    *
    * @param ipAddress: the host IP address
    * @param message: the message to be sent
    */
  private def sendNotificationMessage(ipAddress: String, message: Any): Unit = {

    val msgType: String = ActorsUtils.messageType(message)

    println("Send notification message " + msgType + " to : " + ipAddress)

    // this message is received from the notification handler actor of the client
    val receiver: ActorSelection = context.actorSelection("akka.tcp://DpacClient@" + ipAddress + ":2554" + "/user/fromServerCommunication")
    receiver ! message
  }

  //todo: da testare
  /** Send a message to all remote host of the match of the client with the specified IP.
    *
    * @param ip: the host IP address
    * @param message: the message to be sent
    */
  private def broadcastMessage(ip: String, message: JSONObject): Unit = {
    val ipList: List[String] = getMatchFor(ip).get.involvedPlayerIP

    if (ipList.nonEmpty){
      println("broadcast message to " + ipList.size + " client")
      ipList.foreach((x) => sendRemoteMessage(x,message))
    }
  }

  //todo: da testare
  /** Send a configuration message to all remote host of the match of the client with the specified IP.
    *
    * @param ip: the host IP address
    * @param message: the message to be sent
    */
  private def broadcastConfigurationMessage(ip: String, message: JSONObject): Unit = {
    val ipList: List[String] = getMatchFor(ip).get.involvedPlayerIP

    if (ipList.nonEmpty){
      println("broadcast configuration message to " + ipList.size + " client")
      ipList.foreach((x) => sendConfigurationMessage(x,message))
    }
  }


  //todo: da testare
  /** Send a notification message to all remote host of the match of the client with the specified IP except him.
    *
    * @param excludedClient: the excluded host IP address
    * @param message: the message to be sent
    */
  private def notifyOtherClient(excludedClient: String, message: Any): Unit =  {
    val ipList: List[String] = getMatchFor(excludedClient).get.involvedPlayerIP

    if (ipList.nonEmpty){
      println("sending notification to " + (ipList.size -1) + " clients")
      ipList.foreach((x) => if (x != excludedClient) sendNotificationMessage(x,message))
    }
  }


  private def getClient(username: String): Option[Client] =  {
    onlineClient.find((x) => x.username == username)
  }

  private def getMatchFor(ip: String): Option[Match] = {
    val selected: Option[Match] = onlineMatch.find((x) => x.involvedPlayerIP.contains(ip))

    if( selected.isDefined) {
      println("selected match n° " + selected.get.id)
    }

    selected
  }


}