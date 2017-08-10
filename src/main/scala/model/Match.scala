package model

import java.net.InetAddress

import scala.util.Random

/**
  * Created by Manuel Bottax on 26/07/2017.
  */

class Match (var involvedPlayer: List[String], val size: Range){

  val id: Int = scala.util.Random.nextInt

  var readyPlayer: Int = 0

  def addPlayer (player: String): Unit = {
    involvedPlayer = involvedPlayer ::: List(player)
  }

  def addReadyPlayer(player: String): Unit = {
    if (involvedPlayer contains player) {
      readyPlayer = readyPlayer + 1
    }
  }

  def canStart: Boolean = readyPlayer == involvedPlayer.size

}
