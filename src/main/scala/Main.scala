import actors.UserMasterActor.LoginMessage
import actors.{MessageDispatcherActor, MessageReceiverActor, UserMasterActor}
import akka.actor.{ActorRef, ActorSystem, Props}

/**
  * Created by Manuel Bottax on 15/07/2017.
  */
object Main {

  def main(args: Array[String]): Unit = {

    val defaultPort: String = "8080"

    val port: String = if (args.length > 0) args(0).toString else defaultPort

    val system = ActorSystem("DpacServer")

    val messageReceiver: ActorRef = system actorOf(Props[MessageReceiverActor] , "messageReceiver")
    val messageDispatcher: ActorRef = system actorOf(Props[MessageDispatcherActor], "messageDispatcher")
    val userMaster: ActorRef = system actorOf(Props[UserMasterActor], "userMaster")


    userMaster ! LoginMessage ("testUser", "pswd")
  }
}
