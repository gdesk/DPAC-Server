package utils

import java.io.{File, PrintWriter}
import java.util.Calendar

import clientModel.model.gameElement._
import clientModel.model.utilities.{Dimension, PointImpl}
import clientModel.model.{Playground, PlaygroundImpl}

import scala.collection.mutable.ListBuffer
import scala.io.Source

/** Utils for handle file I/O.
  *
  * @author manuBottax
  */
object IOUtils {

  private val BASE_PATH = "src/main/resources/playground/"
  private val PLAYGROUND_FILE_EXTENSION = ".dpac"

  private val writer: PrintWriter = new PrintWriter(new File("log.txt"))

  /** Utils method for save a string on file, used as a logger feature for the application.
    *
    * @param log the string to be saved
    */
  def saveLog(log: String): Unit = {
    println("log ricevuto: " + log)
    val cal: Calendar = Calendar.getInstance()
    writer.append("[ " + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR)
      + " " + cal.get(Calendar.HOUR) + ":" +
      cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + " ]: " + log + "\n")
    writer.flush()
    //writer.close()
  }


  /** Get the specified file from path and parse it to generate a playground.
    *
    * the syntax for that file is :
    * 'x' -> block
    * '.' -> dot
    * 'p' -> pill
    *
    * 'a' -> apple
    * 'b' -> bell
    * 'c' -> cherry
    * 's' -> Galaxian Ship
    * 'g' -> grapes
    * 'k' -> key
    * 'o' -> orange
    * 's' -> strawberry
    *
    * other char are taken as blank space by default.
    *
    * dimension are taken from file counting the line and the length of the line in it.
    *
    * @param file the file to be parsed
    * @return the playground parsed from file
    *
    */
  def getPlaygroundFromFile(file: File): Playground = {
    val playground: Playground = PlaygroundImpl.instance()

    if (file.canRead) {
      val block: List[Block] = parseBlock(file)
      val eatable: List[Eatable] = parseEatable(file)

      playground.dimension = parseDimension(file)
      val groundList: List[GameItem] = block ::: eatable
      playground.ground_=(groundList)
    }

    playground
  }

  /** Get the specified file from path and parse it to generate a playground.
    *
    * the syntax for that file is :
    * 'x' -> block
    * '.' -> dot
    * 'p' -> pill
    *
    * 'a' -> apple
    * 'b' -> bell
    * 'c' -> cherry
    * 's' -> Galaxian Ship
    * 'g' -> grapes
    * 'k' -> key
    * 'o' -> orange
    * 's' -> strawberry
    *
    * other char are taken as blank space by default.
    *
    * dimension are taken from file counting the line and the length of the line in it.
    *
    * @param fileName the name of the file to be parsed. Suggested format is ''filename.dpac''
    * @return the playground parsed from file
    */
  def getPlaygroundFromPath(fileName: String): Playground = {
    val playgroundFile: File = new File(BASE_PATH + fileName)
    getPlaygroundFromFile(playgroundFile)
  }

  private def parseBlock(file: File): List[Block] = {
    var blockList: ListBuffer[Block] = new ListBuffer[Block]
    var xPosition: Int = 0
    var yPosition: Int = 0

    Source.fromFile(file).foreach(_ match {
      case 'x' => {
        xPosition = xPosition + 1
        blockList.+=(Block(PointImpl(xPosition, yPosition)))
      }
      case '\n' => {
        yPosition = yPosition + 1
        xPosition = 0
        false
      }
      case _ => {
        xPosition = xPosition + 1
        false
      }
    })

    blockList.toList
  }


  private def parseEatable(file: File): List[Eatable] = {
    var eatableList: ListBuffer[Eatable] = new ListBuffer[Eatable]
    var xPosition: Int = 0
    var yPosition: Int = 0

    Source.fromFile(file).foreach(_ match {
      case '.' => {
        xPosition = xPosition + 1
        eatableList.+=(Dot("", PointImpl(xPosition, yPosition)))
      }
      case 'p' => {
        xPosition = xPosition + 1
        eatableList.+=(Pill("", PointImpl(xPosition, yPosition)))
      }
      case 'a' => {
        xPosition = xPosition + 1
        eatableList.+=(Apple("", PointImpl(xPosition, yPosition)))
      }
      case 'b' => {
        xPosition = xPosition + 1
        eatableList.+=(Bell("", PointImpl(xPosition, yPosition)))
      }
      case 'c' => {
        xPosition = xPosition + 1
        eatableList.+=(Cherry("", PointImpl(xPosition, yPosition)))
      }
      case 's' => {
        xPosition = xPosition + 1
        eatableList.+=(GalaxianShip("", PointImpl(xPosition, yPosition)))
      }
      case 'g' => {
        xPosition = xPosition + 1
        eatableList.+=(Grapes("", PointImpl(xPosition, yPosition)))
      }
      case 'k' => {
        xPosition = xPosition + 1
        eatableList.+=(Key("", PointImpl(xPosition, yPosition)))
      }

      case 'o' => {
        xPosition = xPosition + 1
        eatableList.+=(Orange("", PointImpl(xPosition, yPosition)))
      }

      case 's' => {
        xPosition = xPosition + 1
        eatableList.+=(Strawberry("", PointImpl(xPosition, yPosition)))
      }

      case '\n' => {
        yPosition = yPosition + 1
        xPosition = 0
        false
      }
      case _ => {
        xPosition = xPosition + 1
        false
      }
    })

    eatableList.toList
  }

  private def parseDimension(file: File): Dimension = {

    var xPosition: Int = 0
    var yPosition: Int = 0

    var xDim: Int = 0
    var yDim: Int = 0

    Source.fromFile(file).foreach(_ match {
      case '\n' => {
        if (xPosition > xDim)
          xDim = xPosition
        xPosition = 0
        yPosition = yPosition + 1
      }

      case _ => {
        xPosition = xPosition + 1
      }
    })

    yDim = yPosition

    Dimension(xDim, yDim)
  }

}