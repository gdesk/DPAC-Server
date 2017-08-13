package actors

import java.io.File
import akka.actor.UntypedAbstractActor
import utils.{ActorsUtils, Utils}
import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.JSONObject

/** Actor that manage the choice of a playground for the match.
  *
  *  @author manuBottax
  */
class PlaygroundManagerActor extends UntypedAbstractActor {

  //todo: questa classe non gestisce più partite in concorrenza

  val availablePlayground: List[File] = getAvailablePlayground

  var currentVoteCount: Int = 0
  var votedPlayground: ListBuffer[Int] = new ListBuffer

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

      // request for the list for all the available playgrounds.
    case "playgrounds" => {
      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("Request for all the available playgrounds images")

      var playgroundImages: Map[Int, Array[Byte]] = Map()
      var i: Int = 0

      for( x <- availablePlayground ){
        playgroundImages += (i -> Utils.getByteArrayFromPlayground(x))
        votedPlayground += i
        i = i + 1
      }

      println("Found " + playgroundImages.size + "playgrounds")

      sender() ! JSONObject(Map[String, Any](
                 "object" -> "AvailablePlaygrounds",
                 "list" -> playgroundImages ,
                 "senderIP" -> senderIP))
    }

      // todo: da testare
      // handle the votation for a playground from a client
    case "chosenPlayground" => {
      val vote: Int = message.asInstanceOf[JSONObject].obj("playground").asInstanceOf[Int]

      votedPlayground(vote) = votedPlayground(vote) + 1
      currentVoteCount = currentVoteCount + 1

      // request the size for the current match
      context.actorSelection("user/messageDispatcher") ! JSONObject(Map[String, Any](
        "object" -> "getMatchSize",
        "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString))

    }

      // complete the handle of the votation after retrieving the player number for this match.
    case "matchSize" => {

      val matchSize: Int = message.asInstanceOf[JSONObject].obj("matchSize").asInstanceOf[Int]

        // if everyone has voted send the chosen playground.
      if (currentVoteCount == matchSize){
        println("Selected Playground !")
        val playground: File = getSelectedPlayground(votedPlayground)
        sender() ! JSONObject(Map[String, Any](
                    "object" -> "playgroundChosen",
                    "playground" -> playground ,
                    "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))

        //todo: da testare se la reinizializzazione da problemi
        votedPlayground = new ListBuffer
        currentVoteCount = 0
      }

      else
        println("Waiting for other votes: " + (matchSize - currentVoteCount) + " votes left.")
    }

    case _ => println(getSelf() + "received unknown message: " + ActorsUtils.messageType(message))
  }

  private def getAvailablePlayground: List[File] = {

    var fileList: List[File] = List()

    val folder = new File("src/main/resources/playground/")
    val listOfFiles = folder.listFiles

    for (file <- listOfFiles) {
      if (file.isFile) {
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
