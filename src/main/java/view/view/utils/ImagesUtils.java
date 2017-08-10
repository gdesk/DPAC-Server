package view.view.utils;


import clientModel.model.gameElement.Eatable;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by chiaravarini on 03/07/17.
 */
public class ImagesUtils {

    public static Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    public static FruitsImages getFruitsImage(Eatable type/*Eatables type*/){
        switch (type.getClass().getSimpleName()){
            case "Cherry":
                return FruitsImages.CHERRY;

            case "Strawberry":
                return FruitsImages.STRAWBERRY;

            case "Orange":
                return FruitsImages.ORANGE;

            case "Apple":
                return FruitsImages.APPLE;

            case "Grapes":
                return FruitsImages.GRAPES;

            case "GalaxianShip":
                return FruitsImages.GALAXIAN;

            case "Bell":
                return FruitsImages.BELL;

            case "Key":
                return FruitsImages.KEY;

        }
        return null;
    }
}
