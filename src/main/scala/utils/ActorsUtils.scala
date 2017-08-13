package utils

import scala.util.parsing.json.JSONObject

/** Utils used by actors to help handle JSONObject.
  *
  * @author manuBottax
  */
object ActorsUtils {

  /** Return the parameter 'object' of the message as a string.
    *
    * @param message: the JSON Message
    * @return the 'object' parameter of the message
    */
  def messageType(message: Any): String = {
    message match {
      case msg: JSONObject => msg.asInstanceOf[JSONObject].obj("object").toString

      case _ => "Unknown message"
    }
  }

  /** Get IP from INetAddress host information.
    *
    * @param host: the host address ( in format <ipAddress>:<port> )
    * @return the IP address as a string
    */
  def parseIP(host: String): String = {
    val stringList: Array[String] = host.split('/')
    stringList(1)
  }

}
