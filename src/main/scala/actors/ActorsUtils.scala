package actors

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.{ByteArrayOutputStream, File}
import javax.imageio.ImageIO


import scala.util.parsing.json.JSONObject

/**
  * Created by Manuel Bottax on 24/07/2017.
  */
object ActorsUtils {

  def messageType(x: Any): String = {
    x match {
      case msg: JSONObject => msg.asInstanceOf[JSONObject].obj("object").toString

      case _ => "Unknown message"
    }
  }

  def parseIP(host: String): String = {
    val stringList: Array[String] = host.split('/')
    stringList(1)
  }

  def toBufferedImage(src: Image): BufferedImage = {

    //todo: aggiusta
    val w = src.getWidth(null)
    val h = src.getHeight(null)
    val `type` = BufferedImage.TYPE_INT_ARGB

    val dest = new BufferedImage(1024,1024,`type`)
    val g2 = dest.createGraphics
    g2.drawImage(src, 0, 0, null)
    g2.dispose()
    dest
  }

  def toByteArray(src: Image): Array[Byte] = {
    var imageInByte: Array[Byte] = Array()

    val originalImage: BufferedImage = toBufferedImage(src)

    // convert BufferedImage to byte array
    val baos: ByteArrayOutputStream = new ByteArrayOutputStream()
    ImageIO.write(originalImage, "png", baos)
    baos.flush()
    imageInByte = baos.toByteArray
    baos.close()

    imageInByte
  }
}

@SerialVersionUID(42l)
case class MyImage() extends BufferedImage (24, 24, BufferedImage.TYPE_INT_ARGB) with Serializable
