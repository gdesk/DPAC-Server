package actors

import akka.actor.{Actor, ActorRef, Props, UntypedAbstractActor}

import scala.util.parsing.json.JSONObject

/** Actor that receive message from the net, parse it and send appropriate message to the actor in the server.
  *
  * @author manuBottax
  */
class MessageReceiverActor (val messageDispatcher: ActorRef) extends UntypedAbstractActor {

  var userMaster: ActorRef = _
  var matchMaster: ActorRef = _

  override def preStart(): Unit = {

    userMaster = context.actorOf(UserMasterActor.props(messageDispatcher) , "userMaster")
    matchMaster = context.actorOf(MatchMasterActor.props(messageDispatcher) , "matchMaster")
    super.preStart()
  }



  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "newUser" => userMaster ! message

    case "login" => userMaster ! message

    case "allMatchResult" => userMaster ! message

    case "rangesRequest" => matchMaster ! message

    case "selectedRange" => matchMaster ! message

    /// PeerBootstrap
    case "startGame" => matchMaster ! message

    case "characterToChooseRequest" => matchMaster ! message

    case "chooseCharacter" => matchMaster ! message

    case "playgrounds" => matchMaster ! message

    case "chosenPlayground" => matchMaster ! message

    case "matchResult" => matchMaster ! message

      //todo: Se fosse una stringa sarebbe meglio
    case "serverIsRunning" => matchMaster ! message



    //case "startGame" => matchMaster ! message

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

}

object MessageReceiverActor {

  /**
    * Create Props for an actor of this type.
    *
    * @param clientMessageDispatcher the reference to the actor that send message to the client
    * @return a Props for creating this actor.
    */
  def props(clientMessageDispatcher: ActorRef): Props = Props(new MessageReceiverActor(clientMessageDispatcher))

}
