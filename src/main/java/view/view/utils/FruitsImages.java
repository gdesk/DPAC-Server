package view.view.utils;

/**
 * Created by chiaravarini on 03/07/17.
 */
public enum FruitsImages {

    APPLE("apple"),
    BELL("bell"),
    CHERRY("cherry"),
    GALAXIAN("galaxian"),
    GRAPES("grapes"),
    KEY("key"),
    ORANGE("orange"),
    STRAWBERRY("strawberry");

    private final String imageFileName;

    FruitsImages(final String imageFileName){
        this.imageFileName = imageFileName;
    }

    public String getImageFileName(){
        return this.imageFileName;
    }
}
