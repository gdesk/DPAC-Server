package actors

import java.io.File
import akka.actor.UntypedAbstractActor
import utils.{ActorsUtils, Direction}
import model.Character
import scala.util.parsing.json.JSONObject

/** Actor that manage the choice of characters for the match.
  *
  *  @author manuBottax
  */
class CharacterManagerActor extends UntypedAbstractActor {

  //todo: questa classe non gestisce più partite in concorrenza

  val playableCharacter: List[Character] = getPlayableCharacters
  var availableCharacter: List[Character] = playableCharacter
  var selectedCharacter: List[Character] = List()

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

      // request for the list of available characters.
    case "characterToChooseRequest"  => {

      println("Request for the available characters")

      println("Now available: " + availableCharacter.size)

      var retList: Map[String,Array[Byte]] = Map()

      availableCharacter.foreach( (x) => retList += ((x.name, x.characterMainImage)) )

      sender() ! JSONObject(Map[String, Any](
                "object" -> "characterToChoose",
                "map" -> retList ,
                "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))
    }

      //tell the client if the character chosen is available. if it is, it is assigned to the client and other client for the match are notified.
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

    case "initCharacter" => {

      var associationMap: Map[String, Array[String]] = Map()

      selectedCharacter.foreach(x => associationMap += (x.ownerIP -> Array(x.getType, x.name)))

      println("associationMap size: " + associationMap.size)

      sender() ! JSONObject(Map[String, Any](
        "object" -> "teamCharacterInit",
        "typeCharacter" -> associationMap,
        "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))

    }

      // request for the data of other character chosen for the match
    case "teamCharacterRequest" => {

      println("Request data for the other characters of the match")

      val senderIp: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println ("Request from " + senderIp)

      val requestedName: String = message.asInstanceOf[JSONObject].obj("requestIP").toString

      println("Requested data for : " + requestedName )

      val requestedCharacter: Character = selectedCharacter.find((x) => x.name == requestedName) get

      val characterList: Map[String, Map[String, Array[Byte]]] = Map(requestedCharacter.name -> getCharacterData(requestedCharacter.name))

        sender() ! JSONObject(Map[String, Any](
          "object" -> "characterChosen",
          "map" -> characterList,
          "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))

    }

    case "clear" => {
      //ho finito e quindi reinizializzo le liste ai valori iniziali per poter gestire la prossima partita
      cleanCharacterManager()
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  //todo: Questa lista in un futuro aggiornamento cambierà da utente a utente a seconda di cosa ha sbloccato un giocatore
  private def getPlayableCharacters: List[Character] = {

    //todo: andrà letta da qualche parte la lista dei disponibili, a livello di model, non così....

    //load all the character resources file:
    var charResList: List[Character] = List()
    val pacman: Character = new Character("pacman")

    val basePath: String = "src/main/resources/characters/"

    for( x <- Direction.values()){
      val path24: String = pacman.name.toLowerCase +"/24x24"
      pacman.addResource(new File(basePath + path24 + "/" + x.getDirection + ".png"))

      val path32: String = pacman.name.toLowerCase +"/32x32"
      pacman.addResource(new File(basePath + path32 + "/" + x.getDirection + ".png"))

      val path48: String = pacman.name.toLowerCase +"/48x48"
      pacman.addResource(new File(basePath + path48 + "/" + x.getDirection + ".png"))

      val path128: String = pacman.name.toLowerCase +"/128x128"
      pacman.addResource(new File(basePath + path128 + "/" + x.getDirection + ".png"))
    }

    charResList = charResList ::: List (pacman)

    for (y <- List("blue", "pink", "red", "yellow")) {

      val ghost: Character = new Character(y)

      for (x <- Direction.values()) {
        val path24: String = "ghosts/" + ghost.name.toLowerCase + "/24x24/"
        ghost.addResource(new File(basePath + path24 + "/" + x.getDirection + ".png"))

        val path32: String = "ghosts/" + ghost.name.toLowerCase + "/32x32/"
        ghost.addResource(new File(basePath + path32 + "/" + x.getDirection + ".png"))

        val path48: String = "ghosts/" + ghost.name.toLowerCase + "/48x48/"
        ghost.addResource(new File(basePath + path48 + "/" + x.getDirection + ".png"))

        val path128: String = "ghosts/" + ghost.name.toLowerCase + "/128x128/"
        ghost.addResource(new File(basePath + path128 + "/" + x.getDirection + ".png"))
      }

      charResList = charResList ::: List (ghost)
    }

    charResList
  }

  private def isAvailable(characterID :String, ownerIP: String): Boolean = {
    val character: Option[Character] = availableCharacter.find((x) => x.name == characterID)

    if (character.isDefined){
      availableCharacter = availableCharacter.filterNot((x) => x.name == characterID)
      // assign the character to the player that has choose it.
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

    println("found character data: " + character.get.name + " ( " + character.get.ownerIP + " ) -> element: " + character.get.resourceList.size)


    //if(character.isDefined){
      return character.get.resourceList
    //}
    //else {
     //  return Map()
    //}
  }

  private def cleanCharacterManager(): Unit = {
    selectedCharacter = List()
    availableCharacter = playableCharacter
  }

}
