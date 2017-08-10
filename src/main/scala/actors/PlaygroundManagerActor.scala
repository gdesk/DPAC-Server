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

      //todo: Da completare la creazione delle immagini di un playground
    case "playgrounds" => {
      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("Request for the available playground image")

      var playgroundImages: Map[Int, Array[Byte]] = Map()

      var i: Int = 0

      for( x <- availablePlayground ){
        playgroundImages += (i -> Utils.getImageForPlayground(x))
        votedPlayground += i
        i = i + 1
      }

      println("Found " + playgroundImages.size + "images")


      sender() ! JSONObject(Map[String, Any](
                "object" -> "AvailablePlaygrounds",
                "list" -> playgroundImages ,
                "senderIP" -> senderIP))
    }

    case "chosenPlayground" => {
      val vote: Int = message.asInstanceOf[JSONObject].obj("playground").asInstanceOf[Int]
      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString
      val matchSize: Int = getMatchSize(senderIP)//message.asInstanceOf[JSONObject].obj("playersNumber").asInstanceOf[Int]

      votedPlayground(vote) = votedPlayground(vote) + 1
      currentVoteCount = currentVoteCount + 1

      if (currentVoteCount == matchSize){
        println("Selected Playground !")
        val playground: File = getSelectedPlayground(votedPlayground)
        sender() ! JSONObject(Map[String, Any](
                    "object" -> "playgroundChosen",
                    "playground" -> playground ,
                    "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))

        //todo: da togliere il commento
        //votedPlayground = new ListBuffer
        currentVoteCount = 0
      }
      else
        println("Waiting for other votes: " + (matchSize - currentVoteCount) + " votes left.")
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

  //todo:
  private def getMatchSize(senderIP: String): Int = 2

}
