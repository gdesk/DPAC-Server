package actors

import akka.actor.{ActorRef, Props, UntypedAbstractActor}
import utils.ActorsUtils

import scala.util.parsing.json.JSONObject

/** Actor that receive message from the remote client, parse it and send appropriate message to the actors in the server.
  *
  * @author manuBottax
  */
class MessageReceiverActor (val messageDispatcher: ActorRef) extends UntypedAbstractActor {

  var userMaster: ActorRef = _
  var matchMaster: ActorRef = _

  /** Start the main Actors that handle user-related message and match-related message */
  override def preStart(): Unit = {
    userMaster = context.actorOf(UserMasterActor.props(messageDispatcher), "userMaster")
    matchMaster = context.actorOf(MatchMasterActor.props(messageDispatcher), "matchMaster")

    super.preStart()
  }

  override def onReceive(message: Any): Unit = {

    println("Received a new message [ " + message.asInstanceOf[JSONObject].obj("object")  + " ] !")

    ActorsUtils.messageType(message) match {

        // message received from client when a new user want to register
      case "newUser" => userMaster ! message

        // message received from client when a user want to log in
      case "login" => userMaster ! message

        // message received from client when a user want to log out
      case "logout" => userMaster ! message

        // message received from client when a user want to receive his previous match result
      case "allMatchResult" => userMaster ! message // non usato

        // message received from client when a user want to receive the list of available range
      case "rangesRequest" => matchMaster ! message //ok

        // message received from client when a range is selected. this also add the user to a match
      case "selectedRange" => matchMaster ! message // ok

        // message received from client when a user want to add a friend to a match
        //todo: è da testare
      case "addFriend" => matchMaster ! message

        // message received from client as response to a friend request
        //todo: è da testare
      case "responseFriend" => matchMaster ! message

        // message received from client when a user want to receive the list of available character
      case "characterToChooseRequest" => matchMaster ! message //ok

        // message received from client when a character is selected -> check for availability and assign it
        // todo: non mi ricordo più se notifico la scelta o no.
      case "chooseCharacter" => matchMaster ! message // ok

        // message received from client when a user want to receive the list of all the character for current match
        // todo: da testare
      case "teamCharacterRequest" => matchMaster ! message

        // message received from client when a user want to receive the list of image of available playground
      case "playgrounds" => matchMaster ! message // ok

        // message received from client when a playground is selected -> check for availability and assign it
      case "chosenPlayground" => matchMaster ! message // ok

        // message received from client when all configuration are completed and the peer need to start
      case "startGame" => matchMaster ! message

        // message received from client when the peer startup is completed and can start -> when all are ready notify all for match start
      case "serverIsRunning" => matchMaster ! message

        // message received from client when the match end -> used to save the result into the database
      case "matchResult" => matchMaster ! message

      case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))

    }
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
