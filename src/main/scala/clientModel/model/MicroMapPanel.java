package clientModel.model;

import clientModel.model.gameElement.Block;
import clientModel.model.utilities.PointImpl;
import utils.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * This class represents the small map of the labyrinth
 * located at the bottom right
 * Created by Chiara Varini on 10/07/17.
 */
public class MicroMapPanel extends JPanel {

    private final static int DIMENTION_DIVIDER = 3;
    private final static int BOUND = 30;
    private final Playground playground;
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final MazePecePanel[][] panles;

    public MicroMapPanel(Playground playground){
        this.playground = playground;
        int columns = playground.dimension().x();
        int rows = playground.dimension().y();
        this.panles = new MazePecePanel[columns+1][rows];

        //setSize(new Dimension(new MazePecePanel().getPreferredSize().width*columns+BOUND/2, new MazePecePanel().getPreferredSize().width*rows+BOUND/2));
        setBorder(BorderFactory.createLineBorder(Color.white));
        setSize(512,512);
        //setBounds((int) MainFrame.DIMENSION.getWidth()-getWidth()-BOUND, (int) MainFrame.DIMENSION.getHeight()-getHeight()-BOUND,getWidth(),getHeight());
        setLayout(new GridBagLayout());

        initMicroMap(columns,rows);
        System.out.println("ciao");

        revalidate();
        repaint();
    }


    private void initMicroMap(final int columns, final int rows){
        int dim = Math.min(512/columns, 512/rows);
        for(int x = 0; x<=columns; x++){
            for(int y = 0; y<rows; y++){
                gbc.gridx = x;
                gbc.gridy = y;
                MazePecePanel panel = new MazePecePanel();
                panel.setSize(dim,dim);
                add(panel,gbc);
            }
        }
    }

    private class MazePecePanel extends JPanel{

        private MazePecePanel(){
            Block fakeBlock = new Block(new PointImpl<Object, Object>(gbc.gridx,gbc.gridy));
            //setBorder(BorderFactory.createLineBorder(Color.BLACK));
            if( Utils.getJavaList(playground.blocks()).contains(fakeBlock)) {
                this.setBackground(Color.BLACK);
            } else {
                this.setBackground(Color.WHITE);
            }
        }
    }
}
