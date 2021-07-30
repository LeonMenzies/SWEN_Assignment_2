package Cells;

import java.awt.*;

/***
 * The player cell represents each player on the board, this is just an extension of cell with a name for the player
 */
public class PlayerCell extends Cells.Cell {

    String name;

    /**
     * Simple constructor for creating a new player cell object
     * @param row the row location on the board
     * @param col the col location on the board
     * @param name the name of the player which this cell is representing
     */
    public PlayerCell(int row, int col, String name, Image cellImage) {
        super(row, col, cellImage);
        this.name = name;
    }

    public Image getCellImage(){
        return super.cellImg;
    }

    /**
     * To string for getting the player cell initials for display
     *
     * @return the players initials
     */
    @Override
    public String toString() {
        return name.substring(0, 2);
    }
}
