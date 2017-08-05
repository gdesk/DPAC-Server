package actors

import akka.actor.UntypedAbstractActor

import scala.util.parsing.json.JSONObject

class ClientManagerActor extends UntypedAbstractActor{

  private var onlineClient: Set[String] = Set()

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))

  }

  private def addPlayer(client: String): Unit = onlineClient = onlineClient + client

  private def removePlayer(client: String): Unit = onlineClient = onlineClient - client

  private def getClient(ipAddress: String): Option[String] = {
    for (x <- onlineClient) {
      if ( x == ipAddress)
        return Option(x)
    }

    Option.empty[String]
  }

  // def getClient(ipAddress: String): Option[Client] = onlineClient.get(ipAddress)

  private def onlinePlayerCount: Int = onlineClient.size
}
