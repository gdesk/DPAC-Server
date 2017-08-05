package actors

import akka.actor.UntypedAbstractActor
import model.Client

import scala.util.parsing.json.JSONObject

class ClientManagerActor extends UntypedAbstractActor{

  private var onlineClient: Set[Client] = Set()

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "addOnlinePlayer" => {
      val user: Client = message.asInstanceOf[JSONObject].obj("player").asInstanceOf[Client]
      addPlayer(user)
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))

  }

  private def addPlayer(client: Client): Unit = onlineClient = onlineClient + client

  private def removePlayer(client: Client): Unit = onlineClient = onlineClient - client

  private def getClient(ipAddress: Client): Option[Client] = {
    for (x <- onlineClient) {
      if ( x == ipAddress)
        return Option(x)
    }

    Option.empty[Client]
  }

  // def getClient(ipAddress: String): Option[Client] = onlineClient.get(ipAddress)

  private def onlinePlayerCount: Int = onlineClient.size
}
