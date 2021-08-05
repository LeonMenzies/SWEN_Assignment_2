package Objects;

import Objects.Estate;

import java.awt.*;

/***
 * A weapon object represents a weapon that is part of the game and can be used in guess of the murder
 */
public class Weapon implements Movable{
    String wepName;

    int row;
    int col;
    private Image weaponImg;
    private Estate estate;

    /**
     * Create a new weapon object
     * @param name the name if the weapon
     * @param row the row of the weapon on the board
     * @param col the col of the weapon on the board
     * @param estate the estate the weapon starts in
     */
    public Weapon(String name, int row, int col, Estate estate, Image wpImg) {
        this.wepName = name;
        this.row = row;
        this.col = col;
        this.estate = estate;
        this.weaponImg = wpImg;
    }

    public Image getImg(){
        return  this.weaponImg;
    }

    /*
     * Getters and setters
     */
    public void setEstate(Estate estate) {
        this.estate = estate;
    }

    public Estate getEstate() {
        return this.estate;
    }

    public String getWepName() {
        return wepName;
    }

    public int getRow(){
        return this.row;
    }

    public int getCol(){
        return this.col;
    }

    public void setRow(int r){
        this.row = r;
    }

    public void setCol(int c){
        this.col = c;
    }

    public Image getCellImage(){
        return this.weaponImg;
    }

    @Override
    public void setCoord(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * The string representation of this weapon object
     * @return The first two letters of weapons name
     */
    @Override
    public String toString() {
        return wepName.substring(0, 2);
    }
}