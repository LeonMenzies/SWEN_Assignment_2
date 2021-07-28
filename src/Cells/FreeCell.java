package Cells;

/***
 * A free cell is any cell a player can walk on
 */
public class FreeCell extends Cells.Cell {

    /**
     * The constructor simply calls a the super constructor with the given row and col
     * @param row row of the free cell on the board
     * @param col col of the free cell on the board
     */
    public FreeCell(int row, int col){
        super(row, col);
    }


    /**
     * Returns a text representation of a free cell on the board of cells
     * @return Return a string that represents the free cell
     */
    @Override
    public String toString() {
        return "__";
    }
}
