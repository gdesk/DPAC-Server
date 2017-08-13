package utils;

/**
 * Enumeration forn different resolution game images.
 *
 * Created by chiaravarini on 01/07/17.
 */

public enum ImagesResolutions {

    RES_24("24x24"),
    RES_32("32x32"),
    RES_48("48x48"),
    RES_128("128x128");

    private final String asString;

    private ImagesResolutions(final String asString){
        this.asString = asString;
    }

    public String asString(){
        return asString;
    }
}
