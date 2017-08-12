package model

import java.net.InetAddress

import scala.util.Random

/**
  * Created by Manuel Bottax on 26/07/2017.
  */

class Match (var involvedPlayer: List[String], val size: Range){

  val id: Int = scala.util.Random.nextInt

  var readyPlayer: Int = 0

  def addPlayer (playerIP: String): Boolean = {
    var addedToMatch: Boolean = false
    val list =  involvedPlayer.find((x) => x == playerIP)

    if(list.isEmpty) {
      involvedPlayer = involvedPlayer ::: List(playerIP)
      addedToMatch = true
    }

    addedToMatch
  }

  def addReadyPlayer(playerIP: String): Unit = {
    if (involvedPlayer contains playerIP) {
      readyPlayer = readyPlayer + 1
      println("New ready player, " + (involvedPlayer.size - readyPlayer) + " left.")
    }
  }

  def canStart: Boolean = readyPlayer == involvedPlayer.size

}
