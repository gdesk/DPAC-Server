package actors

import akka.actor.{ActorRef, ActorSelection, Props, UntypedAbstractActor}
import model.{Client, ClientImpl, ClientManager, MatchResult}

import scala.util.parsing.json.JSONObject

/** Actor that receive message from the server's actors, and send it trough the net to the addressee client.
  *
  * @author manuBottax
  */
class MessageDispatcherActor extends UntypedAbstractActor {


  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "registrationResult" => {

      val res: String = message.asInstanceOf[JSONObject].obj("result").toString
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString
      
      println("registration has " + res)

      var reply: JSONObject = JSONObject(Map[String, Any](
                              "object" -> "registrationResult",
                              "result" -> false ))
      if (res == "success"){
        reply = JSONObject(Map[String, Any](
          "object" -> "registrationResult",
          "result" -> true ))
      }
      else {
        reply = JSONObject(Map[String, Any](
          "object" -> "registrationResult",
          "result" -> false ))
      }

      if (receiver.isDefined) {
          sendRemoteMessage(receiver.get, reply)
      }

    }

    case "loginError" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString
      val receiver: Option[Client] = ClientManager.getClient(ip)

      println("Login has failed ")

      val reply: JSONObject = JSONObject(Map[String, Any](
        "object" -> "matches",
        "list" -> Option.empty[List[MatchResult]]))

      if (receiver.isDefined) {
        sendRemoteMessage(receiver.get, reply)
      }
    }

    case "previousMatchResult" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val receiver: Option[Client] = ClientManager.getClient(ip)

      println("Login ok ! ")

      val reply: JSONObject = JSONObject(Map[String, Any](
        "object" -> "matches",
        "list" -> message.asInstanceOf[JSONObject].obj("list")))

      if (receiver.isDefined) {
        sendRemoteMessage(receiver.get, reply)
      }
    }

    case "ranges" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val receiver: Option[Client] = ClientManager.getClient(ip)

      println("sending available ranges")
      if (receiver.isDefined) {

        val reply: JSONObject = JSONObject(Map[String, Any](
          "object" -> "ranges",
          "list" -> message.asInstanceOf[JSONObject].obj("list")))

        sendRemoteMessage(receiver.get, reply)
      }
    }

    case "characterToChoose" => forwardMessage(message.asInstanceOf[JSONObject])

    case "availableCharacter" => {
      val available: Boolean = message.asInstanceOf[JSONObject].obj("available").asInstanceOf[Boolean]
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString

      val receiver: Option[Client] = ClientManager.getClient(ip)
      println("sending: character is available: " + available)

      if (receiver.isDefined)
        sendRemoteMessage(receiver.get, message)
    }

    case "notifySelection" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString

      notifyOtherClient(ip, message)
    }

    case "AvailablePlaygrounds" => {

      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString

      val receiver: Option[Client] = ClientManager.getClient(ip)
      println("sending available playgrounds")

      if (receiver.isDefined) {

        val reply: JSONObject = JSONObject(Map[String, Any](
          "object" -> "playgrounds",
          "list" -> message.asInstanceOf[JSONObject].obj("list")))

        sendRemoteMessage(receiver.get, reply)
      }
    }

    case "playgroundChosen" => broadcastMessage(message.asInstanceOf[JSONObject])

    case "otherPlayerIP" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString
      val receiver: Option[Client] = ClientManager.getClient(ip)
      println("sending available playgrounds")

      if (receiver.isDefined) {
        sendConfigurationMessage(receiver.get, message)
      }
    }

    case "resultSaved" => forwardMessage(message.asInstanceOf[JSONObject])

      ///// client bootstrap ///////////////////
    case "clientCanConnect" => {
      val reply: JSONObject = JSONObject(Map[String, Any](
        //result" -> PeerBootstrapMessages.CLIENT_CAN_START_RUNNING,
        "result" -> "ClientCanStartRunning" ))

      for (x <- ClientManager.onlineClient) {
        sendConfigurationMessage(x, reply)
        //sendConfigurationMessage(x._2, reply)
      }
    }

    //////////////// LOCAL BEHAVIOUR MESSAGE /////////////////////////

    case "newOnlinePlayer" => {
      val username: String = message.asInstanceOf[JSONObject].obj("username").toString
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      ClientManager.addPlayer(new ClientImpl(username, ip))

      println(s"Player $username connected from $ip ! Now online " + ClientManager.onlinePlayerCount + " players")
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  private def sendRemoteMessage(to: Client, message: Any): Unit = {
    //val clientActorName = "fakeReceiver"
    val clientActorName = "messageReceiver"
    //val receiver: ActorSelection = context.actorSelection("akka.tcp://ClientSystem@" + to.ipAddress + "/user/" + clientActorName)
    //todo come siamo rimasti per le porte ? -> conviene mandare nel messaggio (Ip + porta)
    //val receiver: ActorSelection = context.actorSelection("akka.tcp://DpacServer@" + to.ipAddress + ":4552" + "/user/" + clientActorName)
    val receiver: ActorSelection = context.actorSelection("akka.tcp://DpacClient@" + to.ipAddress + ":2554" + "/user/" + clientActorName)
    receiver ! message
  }

  //todo: invio il messaggio ad uno degli attori che è quello della fede (basta beccarlo con la selection).
  private def sendConfigurationMessage(to: Client, message: Any): Unit = {

    //todo come siamo rimasti per le porte ? -> come faccio a trovare il tuo Thread per mandargli i messaggi ?
    //val receiver: ActorSelection = context.actorSelection("akka.tcp://DpacClient@" + to.ipAddress + ":4552" + "/ClientWorkerThread")
    val receiver: ActorSelection = context.actorSelection("akka.tcp://DpacServer@" + to.ipAddress + ":4552" + "/user/" + "fakeReceiver")
    receiver ! message
  }

  private def forwardMessage(message: JSONObject): Unit = {
    val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString
    val receiver: Option[Client] = ClientManager.getClient(ip)

    println("forwarding message")

    if (receiver.isDefined) {
      sendRemoteMessage(receiver.get, message)
    }
  }

  //todo: questo va fatto solo per la partita interessata
  private def broadcastMessage(message: JSONObject): Unit = {

    for (x <- ClientManager.onlineClient) {
      sendConfigurationMessage(x, message)
      //sendRemoteMessage(x._2, message)
    }
  }

  private def notifyOtherClient(excludedClient: String, message: Any): Unit = {

    for (x <- ClientManager.onlineClient) {
      if (x.ipAddress != excludedClient)
        sendRemoteMessage(x, message)
      /*if (x._1 != excludedClient)
        sendRemoteMessage(x._2, message)*/
    }
  }


}