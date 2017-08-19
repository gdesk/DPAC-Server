package test

import java.io.File

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/** Fake actor system for server testing on local machine.
  *
  * @author manuBottax.
  */
object FakeReceiverMain {

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.parseFile(new File("src/fakeDpacClient.conf"))
    val system = ActorSystem.create("DpacServer", config)

    val fakeReceiver: ActorRef = system actorOf(Props[FakeReceiver], "fakeReceiver")
  }
}
