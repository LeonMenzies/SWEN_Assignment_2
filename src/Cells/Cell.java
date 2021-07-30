package Cells;

import java.awt.*;

/***
 * Cell is an abstract class responsible for all the different cell types that make up the board
 * It only holds the row and column as information
 */
public abstract class Cell {
    int row, col;
    Image cellImg;

    public Cell(int row, int col, Image im) {
        this.row = row;
        this.col = col;
        this.cellImg = im;
    }
    /*
     * Getters
     */
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }


    public Image getCellImage(){
        return cellImg;
    }
}
