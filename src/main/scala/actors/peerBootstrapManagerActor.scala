package actors

import akka.actor.UntypedAbstractActor
import model.{Client, ClientManager, Match, MatchManager}

import scala.util.parsing.json.JSONObject

/**
  * Created by Manuel Bottax on 26/07/2017.
  */
class peerBootstrapManagerActor extends UntypedAbstractActor{



    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))

  }

  private def faiQualcosaCheContaSeCiSonoTutti: Boolean  = true
}
