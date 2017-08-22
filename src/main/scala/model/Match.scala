package model


/** A single match of the game.
  *
  * @param involvedPlayerIP: the list of the IP address of player connected in this match.
  * @param size: the range for this match. it identify the minimum and maximum player number for the match.
  */
class Match (var involvedPlayerIP: List[String], val size: Range){

  val id: Int = scala.util.Random.nextInt(100)
  var
  readyPlayer: Int = 0

  def addPlayer (playerIP: String): Boolean = {
    var addedToMatch: Boolean = false
    val list =  involvedPlayerIP.find((x) => x == playerIP)

    if(list isEmpty) {
      involvedPlayerIP = involvedPlayerIP ::: List(playerIP)
      addedToMatch = true
    }

    addedToMatch
  }

  def removePlayer(playerIP: String): Unit = {
    involvedPlayerIP = involvedPlayerIP.filterNot((x) => x == playerIP)
  }

  def addReadyPlayer(playerIP: String): Unit = {
    if (involvedPlayerIP contains playerIP) {
      readyPlayer = readyPlayer + 1
      println("New ready player, " + (involvedPlayerIP.size - readyPlayer) + " left.")
    }
  }

  def canStart: Boolean = readyPlayer == involvedPlayerIP.size

}
