package Cells;

/***
 * The estate cell class represents the estates on the board
 */
public class EstateCell extends Cells.Cell {

    private boolean isDoor;
    private String nameFirst;
    private String nameSecond;

    public EstateCell(int row, int col, String name1, String name2, Boolean isDoor) {
        super(row, col);
        this.nameFirst = name1;
        this.nameSecond = name2;
        this.isDoor = isDoor;
    }

    /*
     * Getters
     */
    public String getName() {
        return nameFirst + " " + nameSecond;
    }

    /**
     * Check weather this cell is a door cell for exiting the estate
     *
     * @return true or false depending the boolean field
     */
    public boolean isDoor() {
        return isDoor;
    }

    /**
     * This toString disguises the doors as a free cell to make it more visible on the map. otherwise it returns
     * the first character of the first and last name of the estate
     *
     * @return string to be represented on the map
     */
    @Override
    public String toString() {
        if (isDoor) {
            return "__";
        }
        return nameFirst.substring(0, 1) + nameSecond.substring(0, 1);
    }
}
