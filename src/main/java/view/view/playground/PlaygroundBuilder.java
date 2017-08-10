package view.view.playground;

import java.awt.*;

/**
 * Created by manuBottax and chiaravarini on 05/07/17.
 */
public interface PlaygroundBuilder {

    PlaygroundBuilder setColumns(final int colums);
    PlaygroundBuilder setRows(final int rows);
    PlaygroundBuilder setBackground(final Color backgroundColor);
    PlaygroundBuilder setBackground(final Image backgroundImage);
   // Playground setCharacters(final List<Character> characterList);
    //TODO implementa più avanti
    //TODO ripensa a cosa poter mettere

    PlaygroundView createPlayground();

}
