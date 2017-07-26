package model


/**
  * Created by Manuel Bottax on 26/07/2017.
  */

/*
trait ClientManager {

  var onlineClient: Map[String, Client]
  // todo: gestire la disconnessione
  // todo: e se ha più client sulla stessa rete ? (stesso ip)

  def addPlayer(client: Client): Unit

  def removePlayer(client: Client): Unit

  def getClient(ipAddress: String): Option[Client]

}

*/

object ClientManager {

   var onlineClient: Map[String, Client] = Map()

   def addPlayer(client: Client): Unit = onlineClient = onlineClient + (client.ipAddress -> client)

   def removePlayer(client: Client): Unit = onlineClient = onlineClient - client.ipAddress

   def getClient(ipAddress: String): Option[Client] = onlineClient.get(ipAddress)

   def onlinePlayerCount: Int = onlineClient.size
}
