package view.view.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Class for creation of standard view components
 * Created by chiaravarini on 16/07/17.
 */
public class JComponentsUtils {

    public final static Color LOGIN_COLOR = Color.BLACK;
    public final static Color BACKGROUND_COLOR = Color.WHITE;
    public final static int FONT_SIZE = 20;

    public static JPanel createWhitePanel(){
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        return panel;
    }

    public static JPanel createBlackPanel(){
        JPanel panel = new JPanel();
        panel.setBackground(LOGIN_COLOR);
        return panel;
    }

    public static JPanel createTrasparentPanel(){
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0,0,0,0));
        return panel;
    }

    public static JButton createBlackButton(final String name){
        JButton button = new JButton(name);
        button.setBackground(LOGIN_COLOR);
        button.setOpaque(true);
        button.setForeground(BACKGROUND_COLOR);
        button.setBorderPainted(false);
        button.setFont(new Font(button.getFont().getName(), Font.BOLD, FONT_SIZE));
        return button;
    }
}
