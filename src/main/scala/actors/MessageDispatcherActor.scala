package actors

import actors.MessageDispatcherActor.SendMessage
import akka.actor.{Actor, ActorRef, ActorSelection, Props, UntypedAbstractActor}
import model.{Client, ClientImpl, MatchResult, Message}

import scala.util.parsing.json.JSONObject

/** Actor that receive message from the server's actors, and send it trough the net to the addressee client.
  *
  * @author manuBottax
  */
class MessageDispatcherActor extends UntypedAbstractActor {


  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "registrationResult" => {

      val res: String = message.asInstanceOf[JSONObject].obj("result").toString
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString

      val receiver: Client = new ClientImpl("client", ip )
      println( "registration has " + res)

      if (res == "success" ){
        sendMessage(receiver, true)
      }
      else
        sendMessage(receiver, false)



    }

    case "loginError" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString

      val receiver: Client = new ClientImpl("client", ip )

      println( "Login has failed ")

      val reply: JSONObject = JSONObject(Map[String, Any](
                              "object" -> "matches",
                              "list" -> Option.empty[List[MatchResult]]))

      sendMessage(receiver, reply)
    }

    case "previousMatchResult" => {
      val ip: String = message.asInstanceOf[JSONObject].obj("senderIp").toString
      val receiver: Client = new ClientImpl("client", ip )

      println( "Login ok ! ")

      val reply: JSONObject = JSONObject(Map[String, Any](
                              "object" -> "matches",
                              "list" -> message.asInstanceOf[JSONObject].obj("list")  ))

      sendMessage(receiver, reply)
    }

    case _  => println("Unknown message")
  }

  private def sendMessage(to: Client, message: Any): Unit ={
    val clientActorName = "myActor"
    val receiver: ActorSelection = context.actorSelection("akka.tcp://ClientSystem@" + to.ipAddress + "/user/" + clientActorName)

    receiver ! message
  }

}

object MessageDispatcherActor {

  case class SendMessage(receiver: Client)
}