package Gui;

import Cells.Cell;
import Objects.Board;
import Objects.Estate;
import Objects.Player;
import Objects.Weapon;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.List;

/***
 * The BoardCanvas is in charge of displaying the board visually to the player
 */
public class BoardCanvas extends JPanel implements Observer {
    private Board board;
    private Cell[][] cells;
    Map<String, Image> cellImages;
    Map<String, Estate> estates;
    List<Player> players;
    List<Weapon> weapons;
    private int xSize;
    private int ySize;


    public BoardCanvas(Board board, Cell[][] cells, Map<String, Image> cellImages, List<Weapon> wp, Map<String, Estate> estates, List<Player> players) {

        this.board = board;
        this.cells = cells;
        this.cellImages = cellImages;
        this.estates = estates;
        this.players = players;
        this.weapons = wp;
        xSize = 24;
        ySize = 24;
        this.setLayout(new BorderLayout());
    }

    /**
     * paint the components on the JPanel
     *
     * @param g The swing Graphics object
     */
    @Override
    public void paint(Graphics g) {

        //paint the cells on the board
        int xStep = 0;
        int yStep = 0;

        for (Cell[] c1 : cells) {
            for (Cell c2 : c1) {
                g.drawImage(c2.getCellImage(), xStep, yStep, xSize, ySize, null);
                xStep += xSize;
            }
            xStep = 0;
            yStep += ySize;
        }

        //paint the estates on the board
        for (Map.Entry<String, Estate> es : estates.entrySet()) {
            es.getValue().redrawEstate(g);
        }


        //paint the players on the board
        for (Player p : players) {

            //Dont draw the player if in the estate
            g.drawImage(p.getCellImage(), p.getCol() * xSize, p.getRow() * ySize, xSize, ySize, null);

            if (p.getTurn()) {
                g.setColor(new Color(200, 0, 0, 100));
                g.fillOval(p.getCol() * xSize, p.getRow() * ySize, xSize, ySize);
            }

        }
    }

    /**
     * This method is for resizing the estate images
     *
     * @param newX new x location
     * @param newY new y location
     */
    public void updateEstates(int newX, int newY) {
        for (Map.Entry<String, Estate> estates : estates.entrySet()) {
            estates.getValue().updateSize(newX, newY);
        }
    }

    /**
     * This method is for resizing the estates when the window changes size
     *
     * @param newX new x location
     * @param newY new y location
     */
    public void updateSize(int newX, int newY) {
        this.xSize = newX / 24;
        this.ySize = newY / 24;
    }

    /**
     * this method is part of the MVC design and updates the board by repainting the graphics
     */
    @Override
    public void update() {
        cells = board.getCells();
        this.repaint();
    }
}