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

public class BoardCanvas extends JPanel implements Observer {
    private Board board;
    private Cell[][] cells;
    Map<String, Image> cellImages;
    Map<String, Estate> estates;
    List<Player> players;
    List<Weapon> weapons;
    public final int SIZE = 24;

    public BoardCanvas(Board board, Cell[][] cells, Map<String, Image> cellImages, List<Weapon> wp, Map<String, Estate> estates, List<Player> players) {
        this.board = board;
        this.cells = cells;
        this.cellImages = cellImages;
        this.estates = estates;
        this.players = players;
        this.weapons = wp;
    }

    @Override
    public void paint(Graphics g) {


        //paint the cells on the board
        int xStep = 0;
        int yStep = 0;

        for (Cell[] c1 : cells) {
            for (Cell c2 : c1) {
                g.drawImage(c2.getCellImage(), xStep, yStep, SIZE, SIZE, null);
                xStep += SIZE;
            }
            xStep = 0;
            yStep += SIZE;
        }

        //paint the estates on the board
        for (Map.Entry<String, Estate> es : estates.entrySet()) {
            es.getValue().redrawEstate(g);
        }


        //paint the players on the board
        for (Player p : players) {

            //Dont draw the player if in the estate
            g.drawImage(p.getCellImage(), p.getCol() * SIZE, p.getRow() * SIZE, SIZE, SIZE, null);

            if (p.getTurn()) {
                g.setColor(new Color(200, 0, 0, 100));
                g.fillOval(p.getCol() * SIZE, p.getRow() * SIZE, SIZE, SIZE);
            }

        }
    }

    @Override
    public void update() {
        cells = board.getCells();
        this.repaint();
    }
}