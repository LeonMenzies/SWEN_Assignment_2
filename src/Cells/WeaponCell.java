package Cells;

/***
 * THe weapon cell represents a weapon object on the board
 */
public class WeaponCell extends Cells.Cell{
    String weaponName;


    public WeaponCell(int row, int col, String weaponName) {
        super(row, col);
        this.weaponName = weaponName;
    }


    /**
     * create a to string using the first two letters of the weapons name
     * @return the substring of the 2 first letters of the weapons name
     */
    @Override
    public String toString() {
        return weaponName.substring(0, 2);
    }
}
