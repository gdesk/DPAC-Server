package actors

import akka.actor.UntypedAbstractActor
import model.{Client, ClientManager, Match, MatchManager}

import scala.util.parsing.json.JSONObject

/**
  * Created by Manuel Bottax on 26/07/2017.
  */
class peerBootstrapManagerActor extends UntypedAbstractActor{

  var waitingMatch: List[Match] = MatchManager.getWaitingMatch

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "serverIsRunning" => {

      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      val client: Option[Client] = ClientManager.getClient(senderIP)

      if( client.isDefined){
         val currentMatch: Match = MatchManager.getMatchFor(client.get)

        if (faiQualcosaCheContaSeCiSonoTutti) {
          sender() ! JSONObject(Map[String, Any](
                     "object" -> "clientCanConnect",
                     "senderIP" -> senderIP
          ))

        }

      }

      else
        {
          println("MEGAERROREENORME!")
          //throw new MatchNotFoundException
        }




    }

  }

  private def faiQualcosaCheContaSeCiSonoTutti: Boolean  = true
}
