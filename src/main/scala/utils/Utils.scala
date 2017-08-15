package utils

import java.awt.image.BufferedImage
import java.awt.{Image, Toolkit}
import java.io.{ByteArrayOutputStream, File}
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JFrame, JPanel}

import clientModel.model.Playground
import clientModel.view.MicroMapPanel

/** Various utils for image and collection handling.
  *
  * @author chiaravarini and manuBottax.
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

  def getResolution: ImagesResolutions =  Toolkit.getDefaultToolkit.getScreenResolution match{
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

  /** Get a byte array from a playground file.
    * the playground file is transformed into an image and then in a byte array in order to send it through the net.
    * It also save the image into the local file system.
    *
    * @param playgroundFile: the file to be loaded
    * @return the byte array corresponding to the image of the playground.
    */
  def getByteArrayFromPlayground(playgroundFile: File): Array[Byte] = {

    val playground: Playground = IOUtils.getPlaygroundFromFile(playgroundFile)

    val cellRowsSize: Int = 16
    val cellColsSize: Int = 12

    val view: MicroMapPanel = new MicroMapPanel(playground)

    view.setSize(playground.dimension.x * cellColsSize, playground.dimension.y * cellRowsSize)

    val f: JFrame = new JFrame("New Playground Image")
    f.setSize(playground.dimension.x * cellColsSize, playground.dimension.y * cellRowsSize)
    f.setContentPane(view)
    f.setVisible(true)

    val path: String = "src/main/resources/playgroundImages/"

    val dir: File = new File (path)
    dir.mkdir()

    saveComponentAsJPEG(view, f, path + playgroundFile.getName + ".jpg")

    val playgroundImage: Image = new ImageIcon(path + playgroundFile.getName + ".jpg").getImage

    toByteArray(playgroundImage)

  }

  /** Convert an Image to a byte array.
    *
    * @param source: the image
    * @return the corresponding byte array.
    */
  def toByteArray(source: Image): Array[Byte] = {
    var imageInByte: Array[Byte] = Array()

    val originalImage = toBufferedImage(source)

    val outputStream = new ByteArrayOutputStream()
    ImageIO.write(originalImage, "png", outputStream)
    outputStream.flush()
    imageInByte = outputStream.toByteArray
    outputStream.close()

    imageInByte
  }

  private def saveComponentAsJPEG(myComponent: JPanel, frame: JFrame, filename: String): Unit = {

  import java.awt.image.BufferedImage
  import java.io.FileOutputStream
  import com.sun.image.codec.jpeg.JPEGCodec

    val size = myComponent.getSize()

    println("MyComponentSize: " + size.height + " - " + size.width)

    val myImage = new BufferedImage(size.getWidth.toInt,size.getHeight.toInt, BufferedImage.TYPE_INT_RGB)
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

    frame.dispose()
  }

  private def toBufferedImage(src: Image): BufferedImage = {

    val w = src.getWidth(null)
    val h = src.getHeight(null)
    val `type` = BufferedImage.TYPE_INT_ARGB



    val dest = new BufferedImage(w,h,`type`)
    val g2 = dest.createGraphics
    g2.drawImage(src, 0, 0, null)
    g2.dispose()
    dest
  }

}
