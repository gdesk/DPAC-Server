import java.io.File
import java.net.InetAddress

import actors.{ActorsUtils, MessageDispatcherActor, MessageReceiverActor}
import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.{ConfigFactory, ConfigResolveOptions}

import scala.util.parsing.json.JSONObject

/**
  * Created by Manuel Bottax on 15/07/2017.
  */
object Main {

  def main(args: Array[String]): Unit = {

    //val config = ConfigFactory.parseFile(new File("src/dpacServer.conf"))
    val myIP = ActorsUtils.parseIP(InetAddress.getLocalHost.toString)
    println("myIp: " + myIP)

    val config = ConfigFactory.parseString(
      " akka { \n" +
        " actor { \n" +
          " provider = remote\n" +
          "}\n" +
        " remote { \n" +
          " enabled-transports = [\"akka.remote.netty.tcp\"]\n" +
          " netty.tcp { \n" +
            " hostname = \"" + myIP +"\"\n" +
            " port = 2552\n" +
          "}\n" +
        "}\n" +
      "}\n")

    val system = ActorSystem.create("DpacServer", config)

    val messageDispatcher: ActorRef = system actorOf(Props[MessageDispatcherActor] , "messageDispatcher")
    val messageReceiver: ActorRef = system actorOf(MessageReceiverActor.props(messageDispatcher) , "messageReceiver")


    /*

            //test manuale
            messageReceiver ! JSONObject(Map[String, String](
                  "object" -> "newUser",
                  "name" -> "testUser",
                  "username" -> "myUserName",
                  "email" -> "me@mail.com",
                  "password" -> "pswd",
                  "senderIP" -> "127.0.0.1"
                ))


            messageReceiver ! JSONObject(Map[String, String](
              "object" -> "login",
              "username" -> "myUserName",
              "password" -> "pswd",
              "senderIP" -> "127.0.0.1"
            ))

            messageReceiver ! JSONObject(Map[String, String](
              "object" -> "login",
              "username" -> "myUserName2",
              "password" -> "pswd",
              "senderIP" -> "127.0.0.1"
            ))

            messageReceiver ! JSONObject(Map[String, String](
              "object" -> "login",
              "username" -> "myUserName3",
              "password" -> "pswd",
              "senderIP" -> "127.0.0.1"
            ))

            messageReceiver ! JSONObject(Map[String, String](
              "object" -> "login",
              "username" -> "myUserName4",
              "password" -> "pswd",
              "senderIP" -> "127.0.0.1"
            ))

            messageReceiver ! JSONObject(Map[String, String](
              "object" -> "playgrounds",
              "username" -> "myUserName4",
              "password" -> "pswd",
              "senderIP" -> "127.0.0.1"
            ))


            messageReceiver ! JSONObject(Map[String, Any](
              "object" -> "chosenPlayground",
              "playground" -> 1,
              "senderIP" -> "127.0.0.1"
            ))


            messageReceiver ! JSONObject(Map[String, Any](
              "object" -> "chosenPlayground",
              "playground" -> 2,
              "senderIP" -> "127.0.0.1"
            ))


            messageReceiver ! JSONObject(Map[String, Any](
              "object" -> "selectedRange",
              "range" -> Range(1,5),
              "senderIP" -> "127.0.0.1"
            ))

            messageReceiver ! JSONObject(Map[String, Any](
              "object" -> "selectedRange",
              "range" -> Range(1,5),
              "senderIP" -> "127.0.0.1"
            ))

            messageReceiver ! JSONObject(Map[String, Any](
              "object" -> "selectedRange",
              "range" -> Range(1,5),
              "senderIP" -> "127.0.0.1"
            ))



            messageReceiver ! JSONObject(Map[String, Any](
              "object" -> "startGame",
              "senderIP" -> "127.0.0.1"
            ))

            messageReceiver ! JSONObject(Map[String, Any](
              "object" -> "serverIsRunning",
              "senderIP" -> "127.0.0.1"
            ))

            messageReceiver ! JSONObject(Map[String, Any](
              "object" -> "serverIsRunning",
              "senderIP" -> "127.0.0.1"
            ))

            messageReceiver ! JSONObject(Map[String, Any](
              "object" -> "serverIsRunning",
              "senderIP" -> "127.0.0.1"
            ))

            messageReceiver ! JSONObject(Map[String, Any](
              "object" -> "serverIsRunning",
              "senderIP" -> "127.0.0.1"
            ))

            */

  }
}
