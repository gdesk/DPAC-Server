import actors.GameMasterActor.GameEndMessage
import actors.UserMasterActor.LoginMessage
import actors.{GameMasterActor, MessageDispatcherActor, MessageReceiverActor, UserMasterActor}
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
    val gameMaster: ActorRef = system actorOf(Props[GameMasterActor], "gameMaster")


    userMaster ! LoginMessage ("testUser", "pswd")

    gameMaster ! GameEndMessage(42)


    //todo nel companion object ci vanno i messaggi che può ricevere, fare un'altra interfaccia per quelli che invia

  }
}
