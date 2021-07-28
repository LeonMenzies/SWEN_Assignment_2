package Cells;

/***
 * A grey cell is a cell on the board that a player cannot walk on top of
 */
public class GreyCell extends Cells.Cell {

    /**
     * Simple constructor for creating a new gre cell
     * @param row row of the grey cell
     * @param col col of the grey cell
     */
    public GreyCell(int row, int col){
        super(row, col);
    }

    /**
     * The toString for this object simply returns GC which represents a grey cell on the board
     * @return return a text representation of a gery cell
     */
    @Override
    public String toString() {
        return "GC";
    }
}
