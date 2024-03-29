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
    private List<Drawable> objectsInEstate;
    private List<Integer> availableCells;
    private List<Cell> exitCells;
    private Image estateImg;
    private int row, col;
    private int xSize;
    private int ySize;
    private int endX;
    private int endY;



    public Estate(String estateName, List<Integer> availableCells, int row, int col, Image estateImg, int endX , int endY) {
        this.xSize = 24;
        this.ySize = 24;
        this.estateName = estateName;
        this.estateCellList = new ArrayList<>();
        this.exitCells = new ArrayList<>();
        this.playersInEstate = new ArrayList<>();
        this.objectsInEstate = new ArrayList<>();
        this.availableCells = availableCells;
        this.weaponsInEstate = new ArrayList<>();
        this.estateImg = estateImg;
        this.row = row; this.col = col;
        this.endX = endX;
        this.endY = endY;
    }

    /**
     * Add a cell that makes up the estate on the board
     *
     * @param c the cell to add to the arraylist of estate cells
     */
    public void addCell(EstateCell c) {
        this.estateCellList.add(c);
    }


    public void redrawEstate(Graphics g) {



        g.drawImage(estateImg, col * xSize, row * ySize, endX * xSize, endY * ySize, null);


        //Special method for picking the right location to redraw the players in the estate
        for(int i = 0; i < playersInEstate.size() + weaponsInEstate.size(); i++){

            int row = estateCellList.get(availableCells.get(i)).getRow();
            int col = estateCellList.get(availableCells.get(i)).getCol();

            g.drawImage(objectsInEstate.get(i).getCellImage(), col * xSize, row * ySize,  xSize, ySize, null);
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

    public void updateSize(int newX, int newY){
        this.xSize = newX/24;
        this.ySize = newY/24;
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