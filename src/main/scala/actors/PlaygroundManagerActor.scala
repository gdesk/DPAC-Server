package actors

import java.io.File
import java.nio.file.{Files, Paths}
import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration
import akka.actor.UntypedAbstractActor
import akka.pattern.ask
import utils.{ActorsUtils, Utils}

import scala.collection.mutable
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
  var votedPlayground: Map[Int, mutable.ListBuffer[String]] = Map.empty

  var pacmanIP: String = _
  var selectedPlaygroundIndex: Int = -1

  override def onReceive(message: Any): Unit = ActorsUtils.messageType(message) match {

      // request for the list for all the available playgrounds.
    case "playgrounds" => {
      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      println("Request for all the available playgrounds images")

      var playgroundImages: Map[Int, Array[Byte]] = Map()
      var i: Int = 0

      for( x <- availablePlayground ){
        playgroundImages += (i -> Utils.getByteArrayFromPlayground(x))
        votedPlayground += (i -> ListBuffer.empty)
        i = i + 1
      }

      println("Found " + playgroundImages.size + " playgrounds")

      sender() ! JSONObject(Map[String, Any](
                 "object" -> "AvailablePlaygrounds",
                 "list" -> playgroundImages ,
                 "senderIP" -> senderIP))
    }

      // todo: da testare
      // handle the votation for a playground from a client
    case "chosenPlayground" => {
      val vote: Int = message.asInstanceOf[JSONObject].obj("playground").asInstanceOf[Int]
      val senderIP: String = message.asInstanceOf[JSONObject].obj("senderIP").toString

      votedPlayground(vote) += senderIP
      currentVoteCount = currentVoteCount + 1

      // request the size for the current match
      context.actorSelection("/user/messageDispatcher") ! JSONObject(Map[String, Any](
        "object" -> "getMatchSize",
        "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString))

    }

      // complete the handle of the votation after retrieving the player number for this match.
    case "matchSize" => {

      val matchSize: Int = message.asInstanceOf[JSONObject].obj("matchSize").asInstanceOf[Int]

        // if everyone has voted send the chosen playground.
      if (currentVoteCount == matchSize){
        println("Selected Playground !")
        val playground: Array[Byte] = getSelectedPlayground(votedPlayground)
        sender() ! JSONObject(Map[String, Any](
                    "object" -> "playgroundChosen",
                    "playground" -> playground ,
                    "senderIP" -> message.asInstanceOf[JSONObject].obj("senderIP").toString ))

        votedPlayground = Map.empty
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

  private def getSelectedPlayground(votedPlayground: Map[Int, ListBuffer[String]]): Array[Byte] = {

    var max: Int = 0
    var selected: ListBuffer[ListBuffer[String]] = ListBuffer.empty
    var playgroundIndex: ListBuffer[Int] = ListBuffer.empty

    votedPlayground.foreach(x => {
      val ipList = x._2

      if (ipList.size == max) {
        selected += ipList
        max = ipList.size
        playgroundIndex += x._1
      }

      if (ipList.size > max) {
        selected = ListBuffer.empty
        playgroundIndex = ListBuffer.empty
        selected += ipList
        max = ipList.size
        playgroundIndex += x._1
      }
    }
    )

    //there is a tie
    if (selected.size > 1) {

      import scala.concurrent.Await
      import akka.util.Timeout

      implicit val timeout = Timeout(1000,TimeUnit.MILLISECONDS)

      // ask pattern
      val result = context.actorSelection("../characterManager") ? JSONObject(Map[String, Any]("object" -> "getPacmanIP"))

      Await.result(result, Duration(1000, TimeUnit.MILLISECONDS))

      pacmanIP = result.value.get.get.toString

      votedPlayground.keySet.foreach(y => {
        if (votedPlayground(y) contains pacmanIP) {
          selectedPlaygroundIndex = y
        }
      })
    }

    else {
      selectedPlaygroundIndex = playgroundIndex.head
    }

    val file = availablePlayground(selectedPlaygroundIndex)
    Files.readAllBytes(Paths.get(file.getPath))
  }
}
