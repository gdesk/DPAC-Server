package view.view.playground;

import java.awt.*;

/**
 * Created by chiaravarini on 05/07/17.
 */
public class PlaygroundBuilderImpl implements PlaygroundBuilder {

    private int colums = 25;
    private int rows = 25;
    private Color color = Color.black;
    private Image image = null;

    @Override
    public PlaygroundBuilder setColumns(int colums) {
        this.colums = colums;
        return this;
    }

    @Override
    public PlaygroundBuilder setRows(int rows) {
        this.rows = rows;
        return this;
    }

    @Override
    public PlaygroundBuilder setBackground(Color backgroundColor) {
        this.color = backgroundColor;
        return this;
    }

    @Override
    public PlaygroundBuilder setBackground(Image backgroundImage) {
        this.image = backgroundImage;
        return this;
    }

    @Override
    public PlaygroundView createPlayground(){
        PlaygroundSettings settings = new PlaygroundSettings(colums,rows);
        settings.setBackgroundColor(color);
        if(image!=null){
            settings.setBackgroundImage(image);
        }

        return new PlaygroundPanel(settings);
    }
}
