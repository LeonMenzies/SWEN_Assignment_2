package Objects;

import Cells.*;


import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/***
 * The board class holds all the information about the board including its initial state as well as methods to alter its state
 */
public class Board {

    Map<String, Estate> estates = new HashMap<>();
    Map<String, Image> cellImages = new HashMap<>();
    ArrayList<Weapon> weapons = new ArrayList<>();
    List<Player> players = new ArrayList<>();
    Cell[][] cells;

    // @formatter:off
    String boardCells =
                    "|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|\n" +
                    "|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|\n" +
                    "|__|__|HH|HH|HH|HH|HH|__|__|__|__|__|__|__|__|__|__|MM|MM|MM|MM|MM|__|__|\n" +
                    "|__|__|HH|HH|HH|HH|HD|HE|__|__|__|__|__|__|__|__|__|MM|MM|MM|MM|MM|__|__|\n" +
                    "|__|__|HH|HH|HH|HH|HH|__|__|__|__|__|__|__|__|__|__|MM|MM|MM|MM|MM|__|__|\n" +
                    "|__|__|HH|HH|HH|HH|HH|__|__|__|__|GC|GC|__|__|__|ME|MD|MM|MM|MM|MM|__|__|\n" +
                    "|__|__|HH|HH|HH|HD|HH|__|__|__|__|GC|GC|__|__|__|__|MM|MM|MM|MD|MM|__|__|\n" +
                    "|__|__|__|__|__|HE|__|__|__|__|__|__|__|__|__|__|__|__|__|__|ME|__|__|__|\n" +
                    "|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|\n" +
                    "|__|__|__|__|__|__|__|__|__|__|__|__|VE|__|__|__|__|__|__|__|__|__|__|__|\n" +
                    "|__|__|__|__|__|__|__|__|__|VC|VC|VC|VD|VC|VC|__|__|__|__|__|__|__|__|__|\n" +
                    "|__|__|__|__|__|GC|GC|__|__|VC|VC|VC|VC|VC|VD|VE|__|GC|GC|__|__|__|__|__|\n" +
                    "|__|__|__|__|__|GC|GC|__|VE|VD|VC|VC|VC|VC|VC|__|__|GC|GC|__|__|__|__|__|\n" +
                    "|__|__|__|__|__|__|__|__|__|VC|VC|VD|VC|VC|VC|__|__|__|__|__|__|__|__|__|\n" +
                    "|__|__|__|__|__|__|__|__|__|__|__|VE|__|__|__|__|__|__|__|__|__|__|__|__|\n" +
                    "|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|\n" +
                    "|__|__|__|CE|__|__|__|__|__|__|__|__|__|__|__|__|__|__|PE|__|__|__|__|__|\n" +
                    "|__|__|CC|CD|CC|CC|CC|__|__|__|__|GC|GC|__|__|__|__|PP|PD|PP|PP|PP|__|__|\n" +
                    "|__|__|CC|CC|CC|CC|CD|CE|__|__|__|GC|GC|__|__|__|__|PP|PP|PP|PP|PP|__|__|\n" +
                    "|__|__|CC|CC|CC|CC|CC|__|__|__|__|__|__|__|__|__|__|PP|PP|PP|PP|PP|__|__|\n" +
                    "|__|__|CC|CC|CC|CC|CC|__|__|__|__|__|__|__|__|__|PE|PD|PP|PP|PP|PP|__|__|\n" +
                    "|__|__|CC|CC|CC|CC|CC|__|__|__|__|__|__|__|__|__|__|PP|PP|PP|PP|PP|__|__|\n" +
                    "|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|\n" +
                    "|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|\n";
    // @formatter:on


    public Board(int width, int height) {
        cells = new Cell[width][height];
        //Add all the estates to this board with lists of indexes to free storage cells
        try {

            estates.put("Haunted Door", new Estate("Haunted House", new ArrayList<>(Arrays.asList(6, 7, 8, 11, 12, 13, 16, 17, 18)), 2, 2, ImageIO.read(new File("src/resources/estate_haunted_house.png")), 5, 5));
            estates.put("Manic Door", new Estate("Manic Manor", new ArrayList<>(Arrays.asList(6, 7, 8, 11, 12, 13, 16, 17, 18)), 2, 17, ImageIO.read(new File("src/resources/estate_manic_manor.png")), 5, 5));
            estates.put("Peril Door", new Estate("Peril Palace", new ArrayList<>(Arrays.asList(6, 7, 8, 11, 12, 13, 16, 17, 18)), 17, 17, ImageIO.read(new File("src/resources/estate_peril_palace.png")), 5, 5));
            estates.put("Calamity Door", new Estate("Calamity Castle", new ArrayList<>(Arrays.asList(6, 7, 8, 11, 12, 13, 16, 17, 18)), 17, 2, ImageIO.read(new File("src/resources/estate_calamity_castle.png")), 5, 5));
            estates.put("Villa Door", new Estate("Villa Celia", new ArrayList<>(Arrays.asList(7, 8, 9, 10, 13, 14, 15, 16, 17)), 10, 9, ImageIO.read(new File("src/resources/estate_villa_celia.png")), 6, 4));
        } catch (IOException e) {
            System.out.println("Image cannot be found");
        }
    }




    public void loadCellImages() {
        try {
            cellImages.put("__", ImageIO.read(new File("src/resources/free_cell.png")));
            cellImages.put("GC", ImageIO.read(new File("src/resources/grey_cell.png")));

        } catch (IOException e) {
            System.out.println("Invalid cell image");
        }

    }


    /**
     * This method sets up the board before the game starts
     */
    public void setup() {
        loadCellImages();
        Scanner sc = new Scanner(boardCells).useDelimiter("\\|");

        int row = 0;
        int col = 0;

        //The switch checks for the cell type and add it to the 2d array that makes up the board
        while (sc.hasNext()) {

            String next = sc.next();
            switch (next) {
                case "__" -> {
                    cells[row][col] = new FreeCell(row, col, cellImages.get("__"));
                    col++;
                }
                case "GC" -> {
                    cells[row][col] = new GreyCell(row, col, cellImages.get("GC"));
                    col++;
                }
                case "CC" -> {
                    EstateCell cc = new EstateCell(row, col, "Calamity", "Castle", false, cellImages.get("__"));
                    cells[row][col] = cc;
                    estates.get("Calamity Door").addCell(cc);
                    col++;
                }
                case "CD" -> {
                    EstateCell cd = new EstateCell(row, col, "Calamity", "Door", true, cellImages.get("__"));
                    cells[row][col] = cd;
                    estates.get("Calamity Door").addCell(cd);
                    col++;
                }
                case "PP" -> {
                    EstateCell pp = new EstateCell(row, col, "Peril", "Palace", false, cellImages.get("__"));
                    cells[row][col] = pp;
                    estates.get("Peril Door").addCell(pp);
                    col++;
                }
                case "PD" -> {
                    EstateCell pd = new EstateCell(row, col, "Peril", "Door", true, cellImages.get("__"));
                    cells[row][col] = pd;
                    estates.get("Peril Door").addCell(pd);
                    col++;
                }
                case "MM" -> {
                    EstateCell mm = new EstateCell(row, col, "Manic", "Manor", false, cellImages.get("__"));
                    cells[row][col] = mm;
                    estates.get("Manic Door").addCell(mm);
                    col++;
                }
                case "MD" -> {
                    EstateCell md = new EstateCell(row, col, "Manic", "Door", true, cellImages.get("__"));
                    cells[row][col] = md;
                    estates.get("Manic Door").addCell(md);
                    col++;
                }
                case "HH" -> {
                    EstateCell hh = new EstateCell(row, col, "Haunted", "House", false, cellImages.get("__"));
                    cells[row][col] = hh;
                    estates.get("Haunted Door").addCell(hh);
                    col++;
                }
                case "HD" -> {
                    EstateCell hd = new EstateCell(row, col, "Haunted", "Door", true, cellImages.get("__"));
                    cells[row][col] = hd;
                    estates.get("Haunted Door").addCell(hd);
                    col++;
                }
                case "VC" -> {
                    EstateCell vc = new EstateCell(row, col, "Villa", "Celia", false, cellImages.get("__"));
                    cells[row][col] = vc;
                    estates.get("Villa Door").addCell(vc);
                    col++;
                }
                case "VD" -> {
                    EstateCell vd = new EstateCell(row, col, "Villa", "Door", true, cellImages.get("__"));
                    cells[row][col] = vd;
                    estates.get("Villa Door").addCell(vd);
                    col++;
                }
                case "VE" -> {
                    FreeCell ve = new FreeCell(row, col, cellImages.get("__"));
                    cells[row][col] = ve;
                    estates.get("Villa Door").addExitCell(ve);
                    col++;
                }
                case "HE" -> {
                    FreeCell he = new FreeCell(row, col, cellImages.get("__"));
                    cells[row][col] = he;
                    estates.get("Haunted Door").addExitCell(he);
                    col++;
                }
                case "PE" -> {
                    FreeCell pe = new FreeCell(row, col, cellImages.get("__"));
                    cells[row][col] = pe;
                    estates.get("Peril Door").addExitCell(pe);
                    col++;
                }
                case "ME" -> {
                    FreeCell me = new FreeCell(row, col, cellImages.get("__"));
                    cells[row][col] = me;
                    estates.get("Manic Door").addExitCell(me);
                    col++;
                }
                case "CE" -> {
                    FreeCell ce = new FreeCell(row, col, cellImages.get("__"));
                    cells[row][col] = ce;
                    estates.get("Calamity Door").addExitCell(ce);
                    col++;
                }
                default -> {
                    row++;
                    col = 0;
                }
            }
        }

    }


    public Map<String, Image> getCellImages() {
        return cellImages;
    }

    /*
     * Getters and setters
     */

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public void addWeapons(ArrayList<Weapon> wp) {
        this.weapons = wp;
    }

    public List<Weapon> getWeapons() {
        return this.weapons;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public void addPlayer(Player p) {
        this.players.add(p);
    }

    public void removePlayer(Player p) {
        this.players.remove(p);
    }

    public Estate getEstate(String name) {
        return estates.get(name);
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public Map<String, Estate> getEstates() {
        return estates;
    }


    /**
     * This toString is for drawing the board onto the text pane for the user
     * It simply iterates the 2d array and uses each objects toString to draw the correct cell visualizations
     *
     * @return The board as a big string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Cell[] c1 : cells) {

            for (Cell c2 : c1) {
                sb.append("|");
                sb.append(c2.toString());
            }
            sb.append("|");
            sb.append("\n");
        }
        return sb.toString();
    }

}