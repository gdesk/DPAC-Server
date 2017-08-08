package clientModel.model

import java.awt.Image

import utils.Utils
import view.view.utils.FruitsImages

/**
  * Wrapper for the specific images of dots, pills and fruits
  * Created by chiaravarini on 03/07/17.
  */
trait GameObjectView {

  /**
    * @return The image of the dot
    */
  def getDot(): Image

  /**
    * @return The image of the pill
    */
  def getPill(): Image

  /**
    * @param fruit
    * @return The image of the specified fruit
    */
  def getFruit(fruit: FruitsImages): Image

}

class GameObjectViewImpl extends GameObjectView{

  override def getDot(): Image = getImage("dot/"+Utils.getResolution().asString()+"/YellowDot")

  override def getPill(): Image = getImage("pill/"+Utils.getResolution().asString()+"/YellowPill")

  override def getFruit(fruit: FruitsImages): Image = getImage("fruit/"+Utils.getResolution().asString()+"/"+fruit.getImageFileName)

  private def getImage(imageName: String): Image = Utils.getImage(imageName)

}
