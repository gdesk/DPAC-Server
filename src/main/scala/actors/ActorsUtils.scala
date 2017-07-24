package actors

import scala.util.parsing.json.JSONObject

/**
  * Created by Manuel Bottax on 24/07/2017.
  */
object ActorsUtils {

  def messageType(x: Any): String = {
    x match {
      case msg: JSONObject => msg.asInstanceOf[JSONObject].obj("object").toString

      case _ => "Unknown message"
    }
  }

}
