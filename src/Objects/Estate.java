package Objects;

import Cells.Cell;
import Cells.EstateCell;
import java.awt.*;
import java.util.*;
import java.util.List;

/***
 * The estate class stores the information about the cells the estate is made up of as well as the players and weapons contained in its fields
 */
public class Estate {
    private String estateName;
    private List<Weapon> weaponsInEstate;
    private List<Player> playersInEstate;
    private List<Cell> estateCellList;
    private List<Movable> objectsInEstate;
    private List<Integer> availableCells;
    private List<Cell> exitCells;
    private Image estateImg;
    private int row, col;



    public Estate(String estateName, List<Integer> availableCells, int row, int col, Image estateImg) {
        this.estateName = estateName;
        this.estateCellList = new ArrayList<>();
        this.exitCells = new ArrayList<>();
        this.playersInEstate = new ArrayList<>();
        this.objectsInEstate = new ArrayList<>();
        this.availableCells = availableCells;
        this.weaponsInEstate = new ArrayList<>();
        this.estateImg = estateImg;
        this.row = row; this.col = col;
    }

    /**
     * Add a cell that makes up the estate on the board
     *
     * @param c the cell to add to the arraylist of estate cells
     */
    public void addCell(EstateCell c) {
        this.estateCellList.add(c);
    }

    /**
     * Redraw the estate on the board to update it when players enter or exit the estate
     *
     * @param b the board to redraw on
     */
    public void redrawEstate(Board b, Graphics g) {
        int SIZE = 24;

        for (Cell c : estateCellList) {
            b.redrawCell(c.getRow(), c.getCol(), c);
        }

        g.drawImage(estateImg, row * SIZE, col * SIZE, estateImg.getWidth(null) / 2, estateImg.getHeight(null) / 2, null);


        //Special method for picking the right location to redraw the players in the estate
        for(int i = 0; i < playersInEstate.size() + weaponsInEstate.size(); i++){

            int row = estateCellList.get(availableCells.get(i)).getRow();
            int col = estateCellList.get(availableCells.get(i)).getCol();

            g.drawImage(objectsInEstate.get(i).getCellImage(), row * SIZE, col * SIZE, SIZE, SIZE, null);
        }
    }

    /**
     * Add exit cells so weh a player exits the estate knows where to put the player
     *
     * @param exitCell  the exit CEll itself
     */
    public void addExitCell(Cell exitCell) {
        this.exitCells.add(exitCell);
    }

    public boolean containsExit(Cell ex){
        return exitCells.contains(ex);
    }

    /**
     * Add a player object to this estate
     *
     * @param pl the player object to be added
     *
     */
    public void addPlayersInEstate(Player pl) {

        this.objectsInEstate.add(pl);
        this.playersInEstate.add(pl);
    }
    /**
     * Add a weapon object to this estate
     *
     * @param wp the player object to be added
     *
     */

    public void addWeaponInEstate(Weapon wp){
        this.weaponsInEstate.add(wp);
        this.objectsInEstate.add(wp);
        wp.setEstate(this);

    }

    /**
     * Remove a player from this estate object
     *
     * @param aPlayerInEstate the player to be removed
     */
    public void removePlayersInEstate(Player aPlayerInEstate) {
        this.playersInEstate.remove(aPlayerInEstate);
        this.objectsInEstate.remove(aPlayerInEstate);
    }

    /**
     * Remove a weapon from this estate object
     *
     * @param aWeapon player to be removed
     */
    public void removeWeaponInEstate(Weapon aWeapon){
        this.weaponsInEstate.remove(aWeapon);
        this.objectsInEstate.remove(aWeapon);
    }

    public String getEstateName(){
        return estateName;
    }

    public int getRow(){
        return this.row;
    }

    public int getCol(){
        return  this.col;
    }
}