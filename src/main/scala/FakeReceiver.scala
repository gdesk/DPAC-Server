import actors.ActorsUtils
import akka.actor.UntypedAbstractActor

import scala.util.parsing.json.JSONObject

/**
  * Created by Manuel Bottax on 26/07/2017.
  */
class FakeReceiver extends UntypedAbstractActor{

  override def onReceive(message: Any): Unit = message match {

    case x: JSONObject => {
      print("Message: " + x.obj("object") + " - ")
      println("Received: " + x.toString())
    }

    case x: Boolean => println("Received: " + x)
  }

}
