package actors

import akka.actor.{ActorSelection, UntypedAbstractActor}
import model.{Client, ClientImpl, MatchResult}

import scala.util.parsing.json.JSONObject

/** Actor that receive message from the server's actors, and send it trough the net to the addressee client.
  *
  * @author manuBottax
  */
class MessageDispatcherActor extends UntypedAbstractActor {

  var onlineClient: Map[String, Client] = Map()
  // todo: gestire la disconnessione
  // todo: e se ha più client sulla stessa rete ? (stesso ip)


  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "registrationResult" => {

      val res: String = message.asInstanceOf[JSONObject].obj("result").toString
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString

      val receiver: Option[Client] = getClient(ip)
      println( "registration has " + res)

      if ( receiver.isDefined) {
        if (res == "success")
          sendRemoteMessage(receiver.get, true)
        else
          sendRemoteMessage(receiver.get, false)
      }

    }

    case "loginError" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString
      val receiver: Option[Client] = getClient(ip)

      println( "Login has failed ")

      val reply: JSONObject = JSONObject(Map[String, Any](
                              "object" -> "matches",
                              "list" -> Option.empty[List[MatchResult]]))

      if ( receiver.isDefined) {
        sendRemoteMessage(receiver.get, reply)
      }
    }

    case "previousMatchResult" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val receiver: Option[Client] = getClient(ip)

      println( "Login ok ! ")

      val reply: JSONObject = JSONObject(Map[String, Any](
                              "object" -> "matches",
                              "list" -> message.asInstanceOf[JSONObject].obj("list")  ))

      if ( receiver.isDefined) {
        sendRemoteMessage(receiver.get, reply)
      }
    }

    case "ranges" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val receiver: Option[Client] = getClient(ip)

      println( "sending available ranges")
      if ( receiver.isDefined) {

      val reply: JSONObject = JSONObject(Map[String, Any](
        "object" -> "ranges",
        "list" -> message.asInstanceOf[JSONObject].obj("list")  ))

      sendRemoteMessage(receiver.get, reply)
      }
    }

    case "characterToChoose" => forwardMessage(message.asInstanceOf[JSONObject])

    case "availableCharacter" => {
      val available: Boolean = message.asInstanceOf[JSONObject].obj("available").asInstanceOf[Boolean]
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString

      val receiver: Option[Client] = getClient(ip)
      println( "sending: character is available: " + available)

      if ( receiver.isDefined)
          sendRemoteMessage(receiver.get, message)
    }

    case "notifySelection" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString

      for (x <- onlineClient) {
        if ( x._1 != ip )
          sendRemoteMessage(x._2,message)
      }
    }

    case "AvailablePlaygrounds" => {

      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString

      val receiver: Option[Client] = getClient(ip)
      println( "sending available playgrounds" )

      if (receiver.isDefined) {

        val reply: JSONObject = JSONObject(Map[String, Any](
          "object" -> "playgrounds",
          "list" -> message.asInstanceOf[JSONObject].obj("list")  ))

        sendRemoteMessage(receiver.get, reply)
      }
    }

    case "playgroundChosen" => broadcastMessage(message.asInstanceOf[JSONObject])

    case "resultSaved" => forwardMessage(message.asInstanceOf[JSONObject])

      ////////////////// LOCAL MESSAGE //////////////////////////////

    case "newOnlinePlayer" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("IP").toString
      val client: Client = new ClientImpl("client", ip)

      onlineClient += (ip -> client)
      println("New Online User ! Total: " + onlineClient .size)
    }

    case _  => println("Unknown message")
  }

  private def sendRemoteMessage(to: Client, message: Any): Unit ={
    val clientActorName = "MessageReceiverActor"
    //val receiver: ActorSelection = context.actorSelection("akka.tcp://ClientSystem@" + to.ipAddress + "/user/" + clientActorName)
    //todo come siamo rimasti per le porte ? -> conviene mandare nel messaggio (Ip + porta)
    val receiver: ActorSelection = context.actorSelection("akka.tcp://DpacServer@" + to.ipAddress  + ":4552" + "/user/" + clientActorName)
    receiver ! message
  }

  private def forwardMessage (message: JSONObject ): Unit = {
    val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString
    val receiver: Option[Client] = getClient(ip)

    println("forwarding message")

    if (receiver.isDefined) {
      sendRemoteMessage(receiver.get, message)
    }
  }

  private def broadcastMessage (message: JSONObject ): Unit = {
    for (x <- onlineClient) {
      sendRemoteMessage(x._2,message)
    }
  }

  private def getClient (ipAddress: String): Option[Client] ={
    onlineClient.get(ipAddress)
  }

}

object MessageDispatcherActor {

  case class SendMessage(receiver: Client)
}