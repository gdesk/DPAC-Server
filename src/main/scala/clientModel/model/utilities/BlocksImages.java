package clientModel.model.utilities;

import utils.Utils;

import java.awt.*;

/**
 * Created by chiaravarini on 05/07/17.
 */
public enum BlocksImages {

    HORIZONTAL("horizontalBlock"),
    HORIZONTAL_BOTTOM("horizontalBottom"),
    HORIZONTAL_UP("horizontalUp"),

    LEFT_END("leftEnd"),
    LOWER_END("lowerEnd"),
    RIGHT_END("rightEnd"),
    UPPER_END("upperEnd"),

    VERTICAL("verticalBlock"),
    VERTICAL_LEFT("verticalLeft"),
    VERTICAL_RIGHT("verticalRight"),

    LOWER_LEFT_CORNER("lowerLeftCorner"),
    LOWER_RIGHT_CORNER("lowerRightCorner"),
    UPPER_LEFT_CORNER("upperLeftCorner"),
    UPPER_RIGHT_CORNER("upperRightCorner"),

    SINGLE("singleBlock");

    private final String imageFileName;

    BlocksImages(final String imageFileName){
        this.imageFileName = imageFileName;
    }

    public Image getImage(){
        return Utils.getImage("block/"+ Utils.getResolution().asString()+"/"+this.imageFileName);
    }

    public String getImageFileName(){
        return imageFileName;
    }
}

