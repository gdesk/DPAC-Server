package model

import java.awt.Image

import utils.Direction

/**
  * Created by Manuel Bottax on 25/07/2017.
  */
class Character (val name: String) {

  var imageList: Map[Direction,Image] = Map()

  def addImage(d: Direction, img: Image) : Unit = {
    imageList += ((d, img))
  }

}


