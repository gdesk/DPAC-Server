package model

/** A client connected to the server.
  * It is identified by IP and username of the player connected from that client.
  *
  * @author manuBottax
  */
trait Client {
  def IPAddress: String
  def username: String
}

/** A simple implementation for trait [[Client]]
  *
  * @param IPAddress: the IP address of the connected client.
  * @param username: the username of the player connected from that client.
  */
class ClientImpl (val IPAddress: String, val username: String) extends Client