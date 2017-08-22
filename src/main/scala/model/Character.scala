package model

import java.awt.Image
import java.io.File
import javax.swing.ImageIcon

import utils.Utils

/** A class that represent a character on the server.
  *
  * @param name: The id name of the character.
  *
  * @author manuBottax.
  */
class Character (val name: String) {

  var resourceList: Map[String,Array[Byte]] = Map()
  var ownerIP: String = ""

  def getType: String = {
    if (name == "pacman")
      "pacman"
    else
      "ghost"
  }

  def addResource(file: File) : Unit = {
    val image: Image = new ImageIcon (file.getPath).getImage
    val buff: Array[Byte] = Utils.toByteArray(image)

    resourceList += (file.getPath -> buff)
  }

  var characterMainImage: Array[Byte] = {
    name match {
      case "pacman" => Utils.toByteArray(new ImageIcon("src/main/resources/characters/pacman/24x24/Right.png").getImage)
      case x: String => Utils.toByteArray(new ImageIcon("src/main/resources/characters/ghosts/" + x + "/24x24/Right.png").getImage)
    }
  }

}


