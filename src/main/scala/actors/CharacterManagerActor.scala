package actors

import java.awt.Image

import akka.actor.{ActorRef, Props, UntypedAbstractActor}
import model.Direction

import scala.util.parsing.json.JSONObject

/** Actor that manage the choice of a character for the match.
  *
  *  @author manuBottax
  */
class CharacterManagerActor extends UntypedAbstractActor {

  var availableCharacter: Map[String, Image] = getPlayableCharacters
  val playableCharacter: Map[String, Image] = getPlayableCharacters


  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "characterToChooseRequest"  => {

      println("Request for the available character ")

      sender() ! JSONObject(Map[String, Any](
                "object" -> "characterToChoose",
                "map" -> playableCharacter ,
                "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))

    }

      //dico al client se è disponibile e a tutti gli altri dico che è stato scelto
    case "chooseCharacter" => {

      val characterID: String = message.asInstanceOf[JSONObject].obj("character").toString /*getName*/

      println(s"Request if the character $characterID is available ")

      if (isAvailable(characterID)) {
        availableCharacter -= characterID
        val character: Map[String, Map[Direction,Image]] = getCharacterData(characterID)
        sender() ! JSONObject(Map[String, Any](
                    "object" -> "availableCharacter",
                    "available" -> true ,
                    "map" -> character,
                    "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))
        sender() ! JSONObject(Map[String, Any](
                    "object" -> "notifySelection",
                    "character" -> characterID,
                    "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))
      }
      else {
        sender() ! JSONObject(Map[String, Any](
          "object" -> "availableCharacter",
          "available" -> false ,
          "map" -> null,
          "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))
      }
    }

    case "choosenCharacter" => println("ti devo inviare il pacchetto completo del personaggio")

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  //todo
  private def getAvailableCharacter: Map[String, Image] = null

  //todo
  private def getPlayableCharacters: Map[String, Image] = null

  //todo
  private def isAvailable(characterID :String): Boolean = true

  //todo
  private def getCharacterData(characterID: String): Map[String, Map[Direction,Image]]= Map()

}
