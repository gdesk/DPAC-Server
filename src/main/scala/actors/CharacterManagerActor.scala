package actors

import java.awt.Image

import akka.actor.UntypedAbstractActor
import utils.{Direction, Utils}
import model.Character

import scala.util.parsing.json.JSONObject

/** Actor that manage the choice of a character for the match.
  *
  *  @author manuBottax
  */
class CharacterManagerActor extends UntypedAbstractActor {

  val playableCharacter: List[Character] = getPlayableCharacters
  var availableCharacter: List[Character] = playableCharacter

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "characterToChooseRequest"  => {

      println("Request for the available character ")

      println("Now available: " + availableCharacter.size)

      var retList: Map[String,Map[Direction,Image]] = Map()

      for (x <- availableCharacter ) {
        retList += ((x.name, x.imageList))
      }

      sender() ! JSONObject(Map[String, Any](
                "object" -> "characterToChoose",
                "map" -> retList ,
                "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))
    }

      //dico al client se è disponibile e a tutti gli altri dico che è stato scelto
    case "chooseCharacter" => {

      val characterID: String = message.asInstanceOf[JSONObject].obj("character").toString /*getName*/

      println(s"Request if the character $characterID is available ")

      if (isAvailable(characterID)) {

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

  //todo: Questa lista in un futuro aggiornamento cambierà da utente a utente a seconda di cosa ha sbloccato un giocatore
  private def getPlayableCharacters: List[Character] = {


    //TODO: Andrà letta dal database !!!!

    var charResList: List[Character] = List()

    val pacman: Character = new Character("pacman")

    for( x <- Direction.values()){
      var path24: String = pacman.name.toLowerCase +"/24x24"
      pacman.addImage(x,Utils.getCharacterImage(path24 + "/" + x.getDirection))

      var path32: String = pacman.name.toLowerCase +"/32x32"
      pacman.addImage(x,Utils.getCharacterImage(path32 + "/" + x.getDirection))

      var path48: String = pacman.name.toLowerCase +"/48x48"
      pacman.addImage(x,Utils.getCharacterImage(path48 + "/" + x.getDirection))

      var path128: String = pacman.name.toLowerCase +"/128x128"
      pacman.addImage(x,Utils.getCharacterImage(path128 + "/" + x.getDirection))
    }

    charResList = charResList ::: List (pacman)

    for (y <- List("blue", "pink", "red", "yellow")) {

      var ghost: Character = new Character(y)

      for (x <- Direction.values()) {
        var path24: String = "ghosts/" + ghost.name.toLowerCase + "/24x24/"
        pacman.addImage(x, Utils.getImage(path24 + "/" + x.getDirection))

        var path32: String = "ghosts/" + ghost.name.toLowerCase + "/32x32/"
        pacman.addImage(x, Utils.getImage(path32 + "/" + x.getDirection))

        var path48: String = "ghosts/" + ghost.name.toLowerCase + "/48x48/"
        pacman.addImage(x, Utils.getImage(path48 + "/" + x.getDirection))

        var path128: String = "ghosts/" + ghost.name.toLowerCase + "/128x128/"
        pacman.addImage(x, Utils.getImage(path128 + "/" + x.getDirection))
      }

      charResList = charResList ::: List (ghost)
    }

    charResList
  }

  private def isAvailable(characterID :String): Boolean = {
    val character: Option[Character] = availableCharacter.find((x) => x.name == characterID)

    if (character.isDefined){
      availableCharacter = availableCharacter.filterNot((x) => x.name == characterID)
      return true
    }

    false
  }

  private def getCharacterData(characterID: String): Map[String, Map[Direction,Image]]= {

    val character: Option[Character] = playableCharacter.find((x) => x.name == characterID)

    if(character.isDefined){
      return Map((character.get.name, character.get.imageList))
    }

    Map()
  }

}
