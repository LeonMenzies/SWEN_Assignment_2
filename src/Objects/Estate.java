package Objects;

import Cells.Cell;
import Cells.EstateCell;
import Cells.PlayerCell;
import Cells.WeaponCell;

import java.util.*;

/***
 * The estate class stores the information about the cells the estate is made up of as well as the players and weapons contained in its fields
 */
public class Estate {
    private String estateName;
    private List<Weapon> weaponsInEstate;
    private List<Player> playersInEstate;
    private List<Cell> estateCellList;
    public List<Cell> cellObjectsInEstate;
    private List<Integer> availableCells;
    private Map<String, Cell> exitCells;

    public Estate(String estateName, List<Integer> availableCells) {
        this.playersInEstate = new ArrayList<>();
        this.estateName = estateName;
        this.estateCellList = new ArrayList<>();
        this.exitCells = new HashMap<>();
        this.playersInEstate = new ArrayList<>();
        this.cellObjectsInEstate = new ArrayList<>();
        this.availableCells = availableCells;
        this.weaponsInEstate = new ArrayList<>();
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
    public void redrawEstate(Board b) {


        for (Cell c : estateCellList) {
            b.redrawCell(c.getRow(), c.getCol(), c);
        }

        //Special method for picking the right location to redraw the players in the estate
        for(int i = 0; i < playersInEstate.size() + weaponsInEstate.size(); i++){
            b.redrawCell(estateCellList.get(availableCells.get(i)).getRow(), estateCellList.get(availableCells.get(i)).getCol(), cellObjectsInEstate.get(i));
        }
    }



    /**
     * Add exit cells so weh a player exits the estate knows where to put the player
     *
     * @param exitCell  the exit CEll itself
     * @param direction the direction a player must go to get to the exit cell
     */
    public void addExitCell(Cell exitCell, String direction) {
        this.exitCells.put(direction, exitCell);
    }

    /**
     * Add a player object to this estate asw ell as the player cell for drawing
     *
     * @param pl the player object to be added
     * @param pc              the Cell for redrawing in the board
     */
    public void addPlayersInEstate(Player pl, PlayerCell pc) {
        this.cellObjectsInEstate.add(pc);
        this.playersInEstate.add(pl);
    }

    /**
     * Add a player object to this estate
     *
     * @param pl the player object to be added
     *
     */
    public void addPlayersInEstate(Player pl) {

        this.cellObjectsInEstate.add(new PlayerCell(0,0,pl.getName()));
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
        this.cellObjectsInEstate.add(new WeaponCell(0, 0, wp.getWepName()));
        wp.setEstate(this);

    }

    /**
     * When a player tries to exit this method checks weather the given direction is valid
     *
     * @param direction direction the player wants to go
     * @return the cell the player is going to
     */
    public Cell containsExit(String direction) {
        if (exitCells.containsKey(direction)) {
            return exitCells.get(direction);
        }
        return null;
    }

    /**
     * Remove a player from this estate object
     *
     * @param aPlayerInEstate the player to be removed
     */
    public void removePlayersInEstate(Player aPlayerInEstate) {
        this.playersInEstate.remove(aPlayerInEstate);

        Cell toRemove = null;

        //Find the player in the list of estate cells and remove it
        for (Cell c : cellObjectsInEstate) {
            if (c.toString().equals(aPlayerInEstate.toString())) {
                toRemove = c;
            }
        }
        if (toRemove != null) {
            this.cellObjectsInEstate.remove(toRemove);
        }
    }

    /**
     * Remove a weapon from this estate object
     *
     * @param aWeapon player to be removed
     */
    public void removeWeaponInEstate(Weapon aWeapon){
        this.weaponsInEstate.remove(aWeapon);
        Cell toRemove = null;

        //Find the player in the list of estate cells and remove it
        for (Cell c : cellObjectsInEstate) {
            if (c.toString().equals(aWeapon.toString())) {
                toRemove = c;
            }
        }
        if (toRemove != null) {

            this.cellObjectsInEstate.remove(toRemove);
        }
    }

    public String getEstateName(){
        return estateName;
    }
}