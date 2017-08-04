package actors

import akka.actor.UntypedAbstractActor
import model.Client

import scala.util.parsing.json.JSONObject

class ClientManagerActor extends UntypedAbstractActor{

  private var onlineClient: Set[Client] = Set()

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

  }

  private def addPlayer(client: Client): Unit = onlineClient = onlineClient + client

  private def removePlayer(client: Client): Unit = onlineClient = onlineClient - client

  private def getClient(ipAddress: String): Option[Client] = {
    for (x <- onlineClient) {
      if ( x.ipAddress == ipAddress)
        return Option(x)
    }

    Option.empty[Client]
  }

  // def getClient(ipAddress: String): Option[Client] = onlineClient.get(ipAddress)

  private def onlinePlayerCount: Int = onlineClient.size
}
