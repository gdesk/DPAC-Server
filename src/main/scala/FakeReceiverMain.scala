import java.io.File

import actors.MessageDispatcherActor
import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by Manuel Bottax on 26/07/2017.
  */
object FakeReceiverMain {

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.parseFile(new File("src/fakeDpacClient.conf"))
    val system = ActorSystem.create("DpacServer", config)

    val fakeReceiver: ActorRef = system actorOf(Props[FakeReceiver], "fakeReceiver")
  }
}
