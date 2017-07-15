package actors

import actors.MessageDispatcherActor.SendMessage
import akka.actor.Actor

/** Actor that receive message from the server's actors, and send it trough the net to the addressee client.
  *
  * @author manuBottax
  */
class MessageDispatcherActor extends Actor {

  //todo: questo attore sarà quello che riceverà i messaggi dalla rete e li manderà agli attori del server

  def receive = {
    case SendMessage (receiver) => println("Send a message to " + receiver.id )
    case _  => println("Not Implemented yet ")
  }

  private def sendToClient(to: Client, message: Message) = {throw new NotImplementedError()}



}

object MessageDispatcherActor {

  case class SendMessage(receiver: Client)
}