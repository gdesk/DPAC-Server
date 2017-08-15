
import java.net.{InetAddress, NetworkInterface}
import java.util

import actors.{MessageDispatcherActor, MessageReceiverActor}
import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import utils.ActorsUtils

/** The main class for the server application.
  * It configure the server and start actors system.
  *
  * @author manuBottax
  */
object Main {

  def main(args: Array[String]): Unit = {

    println("[ DPACS - Distributed Pacman Server ]")
    println("[ Version 1.0.0 - August 2017 ]")
    println()
    println("[ Project for Software System Development course @ Unibo - Ingegneria e Scienze Informatiche - A.Y. 2016/17 ]")
    println("[ Developed by Manuel Bottazzi, Giulia Lucchi, Federica Pecci, Margherita Pecorelli & Chiara Varini ]")
    println()

    println("-- Server configuration and startup --")
    println()

    //val myIP = ActorsUtils.parseIP(InetAddress.getLocalHost.toString)
    val myIP = ActorsUtils.parseIP(NetworkInterface.getByName("wlan1").getInetAddresses.nextElement().toString)
    println(" -- my IP: " + myIP + " --")

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

    println()
    println("-- Actors creation --")
    println()

    val messageDispatcher: ActorRef = system actorOf(Props[MessageDispatcherActor] , "messageDispatcher")
    println("[ Message dispatcher actor creation completed ]")
    val messageReceiver: ActorRef = system actorOf(MessageReceiverActor.props(messageDispatcher) , "messageReceiver")
    println("[ Message receiver actor creation completed ]")


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
