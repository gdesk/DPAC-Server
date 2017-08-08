package actors

import java.awt.Image
import java.io.File

import akka.actor.UntypedAbstractActor
import utils.Utils

import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.JSONObject

/** Actor that manage the choice of a playground for the match.
  *
  *  @author manuBottax
  */
class PlaygroundManagerActor extends UntypedAbstractActor {

  val availablePlayground: List[File] = getAvailablePlayground

  var currentVoteCount: Int = 0
  var votedPlayground: ListBuffer[Int] = new ListBuffer



  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

    case "playgrounds" => {
      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("Request for the available playground image")

      var playgroundImages: Map[Int, Image] = Map()

      var i: Int = 0

      for( x <- availablePlayground ){
        playgroundImages += ((i, Utils.getImageForPlayground(x)))
        i = i +1
      }

      println("Found " + playgroundImages.size + "images")

      sender() ! JSONObject(Map[String, Any](
                "object" -> "AvailablePlaygrounds",
                "list" -> playgroundImages ,
                "senderIP" -> senderIP))
    }

    case "chosenPlayground" => {
      val vote: Int = message.asInstanceOf[JSONObject].obj("playground").asInstanceOf[Int]
      val playersNumber: Int = message.asInstanceOf[JSONObject].obj("playersNumber").asInstanceOf[Int]

      votedPlayground(vote) = votedPlayground(vote) + 1
      currentVoteCount = currentVoteCount + 1

      if (currentVoteCount == playersNumber){
        val playground: File = getSelectedPlayground(votedPlayground)
        sender() ! JSONObject(Map[String, Any](
                    "object" -> "playgroundChosen",
                    "playground" -> playground ,
                    "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))
      }
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  //todo: andranno presi dal DB
  private def getAvailablePlayground: List[File] = {

    //todo: guarda se c'è modo per aprire tuti i file in una cartella

    var fileList: List[File] = List()

    val folder = new File("src/main/resources/playground/")
    val listOfFiles = folder.listFiles

    for (file <- listOfFiles) {
      if (file.isFile) {
        println(file.getName)
        fileList = fileList ::: List (file)
      }
    }

    fileList
  }

  private def getSelectedPlayground(votedPlayground: ListBuffer[Int]): File = {
    val selected: Int = votedPlayground.indexOf(votedPlayground.max)
    //TODO: gestire i pareggi
    availablePlayground(selected)
  }

}
