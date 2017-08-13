package model

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.{Files, Path, Paths}
import javax.swing.ImageIcon

import utils.{ActorsUtils, Direction, Utils}

/**
  * Created by Manuel Bottax on 25/07/2017.
  */
class Character (val name: String) {

  var imageList: Map[String,Array[Byte]] = Map()
  var ownerIP: String = ""

  def getType: String = {
    if (name == "pacman")
      "pacman"
    else
      "ghost"
  }

  def addImage(file: File) : Unit = {

    val image: Image = new ImageIcon (file.getPath).getImage
    val buff: Array[Byte] = Utils.toByteArray(image)
    imageList += (file.getPath -> buff)
  }

  //var characterImage: Image = new ImageIcon("src/main/resources/characters/pacman/24x24/Right.png").getImage
  var characterImage: File = new File("src/main/resources/characters/pacman/24x24/Right.png")

}


