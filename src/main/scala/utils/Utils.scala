package utils

import java.awt.{Color, Image, Toolkit}
import java.io.File
import java.net.URL
import javax.swing.{ImageIcon, JComponent, JFrame}

import actors.ActorsUtils
import clientModel.model.Playground
import view.view.playground.{PlaygroundBuilderImpl, PlaygroundPanel, PlaygroundSettings, PlaygroundView}
import view.view.utils.ImagesResolutions

/**
  * Created by chiaravarini on 01/07/17.
  */
object Utils {



  val IMAGES_BASE_PATH = "/characters/"
  val IMAGES_EXTENSION = ".png"

  def getResource(path: String): URL = Utils.getClass.getResource(path)   //TODO lanciare eccezione nel caso in cui non trovi la risorsa!

  def getCharacterImage(path: String): Image = {
    val completePath: String = IMAGES_BASE_PATH + path + IMAGES_EXTENSION
    new ImageIcon(getResource(completePath)).getImage
  }

  def getImage(path: String): Image = {
    new ImageIcon(getResource("/images/" + path + ".png")).getImage
  }

  def getResolution(): ImagesResolutions =  Toolkit.getDefaultToolkit().getScreenResolution() match{
    case x if x < 50 =>  ImagesResolutions.RES_24
    case x if x >= 50 && x < 100 =>  ImagesResolutions.RES_32
    case x if x >= 100 && x < 150 =>  ImagesResolutions.RES_48
    case _ =>  ImagesResolutions.RES_128

  }

  def getJavaList[E](list: List[E]): java.util.List[E] = {
    import scala.collection.JavaConverters._
    list.asJava
  }

  def transformInString (array: Array[Char]): String = {
    var res = ""
    array.toSeq.foreach(c=> res += c)
    res
  }

  def getScalaMap[A,B](map: java.util.Map[A,B]): scala.collection.mutable.Map[A,B] = {
    import scala.collection.JavaConverters._
    map.asScala
  }

  def getImageForPlayground(playgroundFile: File): Array[Byte] = {

    val playground: Playground = IOUtils.getPlaygroundFromFile(playgroundFile)

    val view: PlaygroundPanel = new PlaygroundPanel(new PlaygroundSettings(70,30))

    view.renderBlockList(Utils.getJavaList(playground.blocks))
    view.renderEatableList(Utils.getJavaList(playground.eatables))

    val f: JFrame = new JFrame("test")
    f.setSize(1024,1024)
    f.add(view, 0)
    f.setVisible(true)

    saveComponentAsJPEG(view, "" + playgroundFile.getName + ".jpg" )

    val playgroundImage: Image = new ImageIcon("playgroundImages/" + playgroundFile.getName + ".jpg").getImage

    ActorsUtils.toByteArray(playgroundImage)

  }

  private def saveComponentAsJPEG(myComponent: JComponent, filename: String): Unit = {

  import java.awt.image.BufferedImage
  import java.io.FileOutputStream
  import com.sun.image.codec.jpeg.JPEGCodec

    //myComponent.setBackground(Color.white)

    val size = myComponent.getSize()

    if(myComponent == null ) println("ERROEREOROROOER")
    println("ok")
    val myImage = new BufferedImage(size.width,size.height, BufferedImage.TYPE_INT_RGB)
    val g2 = myImage.createGraphics
    myComponent.paint(g2)
    try {
      val out = new FileOutputStream(filename)
      val encoder = JPEGCodec.createJPEGEncoder(out)
      encoder.encode(myImage)
      out.close()
    } catch {
      case e: Exception =>
        System.out.println(e)
    }
  }
}
