package model

/**
  * Created by Manuel Bottax on 26/07/2017.
  */

class Match (var involvedPlayer: List[Client], val size: Range){

  def addPlayer (player: Client): Unit = {
    involvedPlayer = involvedPlayer ::: List(player)
  }

}
