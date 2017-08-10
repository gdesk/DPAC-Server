package clientModel.model

import java.awt.Image

import clientModel.model.utilities.BlocksImages._
import utils.Utils

/**
  * Wrapper for the specific images of each block.
  *
  * Created by chiaravarini on 03/07/17.
  */
trait BlockView {

  /**
    * @return The image of the horizontal block
    */
  def getHorizontal(): Image

  /**
    * @return The image of the horizontal block closed to the left
    */
  def getLeftEnd(): Image

  /**
    * @return The image of the vertical block with the left opening
    */
  def getVerticalLeft(): Image //LeftOpen

  /**
    * @return The image of the vertical block closed at the bottom
    */
  def getLowerEnd(): Image

  /**
    * @return The block image of the lower left corner
    */
  def getLowerLeftCorner(): Image

  /**
    * @return The image of the horizontal block with the lower opening
    */
  def getHorizontalBottom(): Image //LowerOpen

  /**
    * @return The block image of the lower right corner
    */
  def getLowerRightCorner(): Image

  /**
    * @return The image of the horizontal block closed to the right
    */
  def getRightEnd(): Image

  /**
    * @return The image of the vertical block with the right opening
    */
  def getVerticalRight(): Image   //rightOpen

  /**
    * @return The image of the vertical block closed at the top
    */
  def getUpperEnd(): Image

  /**
    * @return The block image of the upper left corner
    */
  def getUpperLeftCorner(): Image

  /**
    * @return The image of the horizontal block with the upper opening
    */
  def getHorizontalUp(): Image  //UpperOpen

  /**
    * @return The block image of the upper right corner
    */
  def getUpperRightCorner(): Image

  /**
    * @return The image of the vertical block
    */
  def getVertical(): Image

  /**
    * @return The image of a single square block
    */
  def getSingle(): Image

}

class BlockViewImpl extends BlockView {


  var path = "block/" + Utils.getResolution().asString() + "/"

  override def getHorizontal(): Image = getImage(HORIZONTAL.getImageFileName())

  override def getLeftEnd(): Image = getImage(LEFT_END.getImageFileName())

  override def getVerticalLeft(): Image = getImage(VERTICAL_LEFT.getImageFileName())

  override def getLowerEnd(): Image = getImage(LOWER_END.getImageFileName())

  override def getLowerLeftCorner(): Image = getImage(LOWER_LEFT_CORNER.getImageFileName())

  override def getHorizontalBottom(): Image = getImage(HORIZONTAL_BOTTOM.getImageFileName())

  override def getLowerRightCorner(): Image = getImage(LOWER_RIGHT_CORNER.getImageFileName())

  override def getRightEnd(): Image = getImage(RIGHT_END.getImageFileName())

  override def getVerticalRight(): Image = getImage(VERTICAL_RIGHT.getImageFileName())

  override def getUpperEnd(): Image = getImage(UPPER_END.getImageFileName())

  override def getUpperLeftCorner(): Image = getImage(UPPER_LEFT_CORNER.getImageFileName())

  override def getHorizontalUp(): Image = getImage(HORIZONTAL_UP.getImageFileName())

  override def getUpperRightCorner(): Image = getImage(UPPER_RIGHT_CORNER.getImageFileName())

  override def getVertical(): Image = getImage(VERTICAL.getImageFileName())

  override def getSingle(): Image = getImage(SINGLE.getImageFileName())

  private def getImage( fileName: String): Image = Utils.getImage(path + fileName)

}
