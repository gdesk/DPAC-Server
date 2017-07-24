package actors

import akka.actor.Actor

/** Actor that manage the result of a match and save it.
  *
  *  @author manuBottax
  */
class GameEndManagerActor extends Actor {

  //todo: questo attore interagisce con il db e va a salvare i risultati della partita per quell'utente


  override def preStart(): Unit = {

    //todo: init()

  }

  def receive = {

    //case GameEndMessage (result) => {
      //println(s"Player has scored $result points")
      //saveResultInDB(result)
    //}

    case _  => println ("received unknown message")
  }

  //todo
  private def saveResultInDB(result: Int): Unit = {}

}

object GameEndManagerActor {
  ////////////////////////////////////////////////////////////////////////////
  // TODO: i messaggi che ricevo ci vanno qui -> quando li mando li importo
  //////////////////////////////////////////////////////////////////////////
  //case class GameEndMessage (result: Int)
  // todo: messaggi di risposta

}
