package clientModel.model

import java.awt.Image

import utils.Utils

/**
  * Wrapper for the specific images of each client.model.character.gameElement.character.
  *
  * Created by chiaravarini on 01/07/17.
  */
trait CharacterView {

  /**
    * @return The client.model.character.gameElement.character's image when it moves upward
    */
  def getCharacterUp: Image

  /**
    * @return The client.model.character.gameElement.character's image when it moves down
    */
  def getCharacterDown: Image

  /**
    * @return The client.model.character.gameElement.character's image when it moves left
    */
  def getCharacterLeft: Image

  /**
    * @return The client.model.character.gameElement.character's image when it moves right
    */
  def getCharacterRight: Image


}

//class CharacterViewImpl(var path:String) extends CharacterView {

class CharacterViewImpl(val characterPath: String) extends CharacterView {

 var pathAndResolution: String = characterPath.toLowerCase()+"/" + Utils.getResolution().asString() + "/"
  
  //resources/images/pacman/24x24/Up.png

  def getCharacterUp: Image = getImage("Up")

  def getCharacterDown: Image = getImage("Down")

  def getCharacterLeft: Image = getImage("Left")

  def getCharacterRight: Image = getImage("Right")

  private def getImage(direction: String): Image = Utils.getImage(pathAndResolution+direction)

}

class RedGhostView extends CharacterViewImpl("ghosts/red")
class BlueGhostView extends CharacterViewImpl("ghosts/blue")
class PinkGhostView extends CharacterViewImpl("ghosts/pink")
class YellowGhostView extends CharacterViewImpl("ghosts/yellow")
class PacmanView extends CharacterViewImpl("pacman")

class CharacterFactory(){

  def createPacman: CharacterView = new PacmanView()
  def createGhost(color: String): CharacterView = color.toLowerCase match {
    case "red" => new RedGhostView()
    case "blue" => new BlueGhostView()
    case "pink" => new PinkGhostView()
    case "yellow" => new YellowGhostView()
  }

}
