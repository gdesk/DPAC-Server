package test

import akka.actor.UntypedAbstractActor

import scala.util.parsing.json.JSONObject

/** Fake actor for server testing on local machine.
  * it simply print all the message received, for debugging purpose.
  *
  * @author manuBottax.
  */
class FakeReceiver extends UntypedAbstractActor{

  override def onReceive(message: Any): Unit = message match {

    case x: JSONObject => {
      print("Message: " + x.obj("object") + " - ")
      println("Received: " + x.toString())
    }

  }

}
