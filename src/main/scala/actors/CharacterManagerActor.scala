package actors

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.{File, FileOutputStream}

import akka.actor.UntypedAbstractActor
import utils.{Direction, Utils}
import model.Character

import scala.util.parsing.json.JSONObject

/** Actor that manage the choice of a character for the match.
  *
  *  @author manuBottax
  */
class CharacterManagerActor extends UntypedAbstractActor {

  //todo: questa classe non gestisce più partite in concorrenza

  val playableCharacter: List[Character] = getPlayableCharacters
  var availableCharacter: List[Character] = playableCharacter
  var selectedCharacter: List[Character] = List()

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "characterToChooseRequest"  => {

      println("Request for the available character")

      println("Now available: " + availableCharacter.size)

      var retList: Map[String,File] = Map()

      availableCharacter.foreach( (x) => retList += ((x.name, x.characterImage)) )

      sender() ! JSONObject(Map[String, Any](
                "object" -> "characterToChoose",
                "map" -> retList ,
                "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))
    }

      //dico al client se è disponibile e a tutti gli altri dico che è stato scelto
    case "chooseCharacter" => {

      val characterID: String = message.asInstanceOf[JSONObject].obj("character").toString
      val senderIp: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println(s"Request if the character $characterID is available ")

      if (isAvailable(characterID, senderIp)) {

        val character: Map[String,Array[Byte]] = getCharacterData(characterID)

        selectedCharacter = selectedCharacter ::: List(getCharacter(characterID))

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


    case "teamCharacterRequest" => {

      println(s"Request data for all the match character")

      var characterList: Map[String, Map[String, Array[Byte]]] = Map()

      selectedCharacter.foreach((x) => characterList += (x.name -> getCharacterData(x.name)) )

      var associationMap: Map[String, Array[String]] = Map()

      selectedCharacter.foreach((x) => associationMap += (x.ownerIP -> Array(x.getType, x.name)) )


        sender() ! JSONObject(Map[String, Any](
          "object" -> "characterChosen",
          "map" -> characterList,
          "typeCharacters" -> associationMap,
          "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))

      //ho finito e quindi reinizializzo le liste ai valori iniziali per poter gestire la prossima partita
      cleanCharacterManager()

    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  //todo: Questa lista in un futuro aggiornamento cambierà da utente a utente a seconda di cosa ha sbloccato un giocatore
  private def getPlayableCharacters: List[Character] = {


    //TODO: Andrà letta dal database ????

    var charResList: List[Character] = List()

    val pacman: Character = new Character("pacman")

    var basePath: String = "src/main/resources/characters/"


    //todo: leggere i file.......
    for( x <- Direction.values()){

      var path24: String = pacman.name.toLowerCase +"/24x24"
      val f: File = new File(basePath + path24 + "/" + x.getDirection + ".png")
      println(f.length())
      pacman.addImage(f)

      var path32: String = pacman.name.toLowerCase +"/32x32"
      pacman.addImage(new File(basePath + path32 + "/" + x.getDirection + ".png"))

      var path48: String = pacman.name.toLowerCase +"/48x48"
      pacman.addImage(new File(basePath + path48 + "/" + x.getDirection + ".png"))

      var path128: String = pacman.name.toLowerCase +"/128x128"
      pacman.addImage(new File(basePath + path128 + "/" + x.getDirection + ".png"))
    }

    charResList = charResList ::: List (pacman)

    for (y <- List("blue", "pink", "red", "yellow")) {

      val ghost: Character = new Character(y)

      for (x <- Direction.values()) {
        val path24: String = "ghosts/" + ghost.name.toLowerCase + "/24x24/"
        ghost.addImage(new File(basePath + path24 + "/" + x.getDirection + ".png"))

        val path32: String = "ghosts/" + ghost.name.toLowerCase + "/32x32/"
        ghost.addImage(new File(basePath + path32 + "/" + x.getDirection + ".png"))

        val path48: String = "ghosts/" + ghost.name.toLowerCase + "/48x48/"
        ghost.addImage(new File(basePath + path48 + "/" + x.getDirection + ".png"))

        val path128: String = "ghosts/" + ghost.name.toLowerCase + "/128x128/"
        ghost.addImage(new File(basePath + path128 + "/" + x.getDirection + ".png"))
      }

      charResList = charResList ::: List (ghost)
    }

    charResList
  }

  private def isAvailable(characterID :String, ownerIP: String): Boolean = {
    val character: Option[Character] = availableCharacter.find((x) => x.name == characterID)

    if (character.isDefined){
      availableCharacter = availableCharacter.filterNot((x) => x.name == characterID)
      character.get.ownerIP = ownerIP
      return true
    }

    false
  }

  private def getCharacter(characterID :String): Character = {
    val character: Option[Character] = playableCharacter.find((x) => x.name == characterID)

    character.get
  }

  private def getCharacterData(characterID: String): Map[String,Array[Byte]] = {

    val character: Option[Character] = playableCharacter.find((x) => x.name == characterID)

    if(character.isDefined){

      return character.get.imageList
    }

    Map()
  }

  private def cleanCharacterManager(): Unit = {
    selectedCharacter = List()
    availableCharacter = playableCharacter
  }

}
