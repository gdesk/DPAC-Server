import java.io.File


import actors.{MessageDispatcherActor, MessageReceiverActor}
import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.parsing.json.JSONObject

/**
  * Created by Manuel Bottax on 15/07/2017.
  */
object Main {

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.parseFile(new File("src/dpacServer.conf"))
    val system = ActorSystem.create("DpacServer", config)

    val messageDispatcher: ActorRef = system actorOf(Props[MessageDispatcherActor], "messageDispatcher")
    val messageReceiver: ActorRef = system actorOf(MessageReceiverActor.props(messageDispatcher) , "messageReceiver")


    //test manuale
    messageReceiver ! JSONObject(Map[String, String](
          "object" -> "newUser",
          "name" -> "testUser",
          "username" -> "myUserName",
          "email" -> "me@mail.com",
          "password" -> "pswd"
        ))

    messageReceiver ! JSONObject(Map[String, String](
      "object" -> "login",
      "username" -> "myUserName",
      "password" -> "pswd"
    ))



  }
}
