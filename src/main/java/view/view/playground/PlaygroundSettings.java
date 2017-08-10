package view.view.playground;

import java.awt.*;

/**
 * Created by Manuel Bottax on 04/07/2017.
 */
public class PlaygroundSettings {

    private final static int DEFAULT_COLUMS_TO_RENDER = 30;
    private final static int DEFAULT_ROWS_TO_RENDER = 30;

    private int columnsToRender;
    private int rowsToRender;

    private int columns;
    private int rows;
    private int cellSize;
    private Dimension cellDim;

    private Color backgroundColor;
    private Image backgroundImage;

    public PlaygroundSettings(final int columns, final int rows){
        this.rows = rows;
        this.columns = columns;

        this.columnsToRender = Math.min(DEFAULT_COLUMS_TO_RENDER,columns);
        this.rowsToRender = Math.min(DEFAULT_ROWS_TO_RENDER,rows);

        int xCellSize = (int)(25);
        int yCellSize = (int)(25);

        this.cellSize = Math.min(xCellSize, yCellSize);
        this.cellDim = new Dimension(cellSize,cellSize);
    }

    public int getCellSize() {return this.cellSize;}

    public int getRows() {return this.rows;}

    public int getColumns() {return this.columns;}

    public Dimension getCellDim() {return this.cellDim;}

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public int getColumnsToRender() {
        return columnsToRender;
    }

    public void setColumnsToRender(int columnsToRender) {
        this.columnsToRender = columnsToRender;
    }

    public int getRowsToRender() {
        return rowsToRender;
    }

    public void setRowsToRender(int rowsToRender) {
        this.rowsToRender = rowsToRender;
    }
}
