package actors

import akka.actor.Actor

/** Actor that receive message from the net, parse it and send appropriate message to the actor in the server.
  *
  * @author manuBottax
  */
class MessageReceiverActor extends Actor {

  //todo: questo attore sarà quello che riceverà i messaggi dalla rete e li manderà agli attori del server

  def receive = {
    //todo: passare riferimenti !
    // a seconda del tipo manda i messaggi a UserMaster o GameMaster
    case _  => println("Not Implemented yet ")
  }


}

object MessageReceiverActor {
  ///
}
