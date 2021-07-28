import Cells.*;

import java.util.*;
import java.util.regex.Pattern;

/***
 * The board class holds all the information about the board including its initial state as well as methods to alter its state
 */
public class Board {

    Map<String, Estate> estates = new HashMap<>();
    Cell[][] cells;
    public final Pattern EXITCELLPATTERN = Pattern.compile("WV|AV|SV|DV|DH|SH|AM|SM|WP|AP|WC|DC");

    // @formatter:off
    String boardCells =
            "|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|\n" +
            "|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|\n" +
            "|__|__|HH|HH|HH|HH|HH|__|__|__|__|__|__|__|__|__|__|MM|MM|MM|MM|MM|__|__|\n" +
            "|__|__|HH|HH|HH|HH|HD|DH|__|__|__|__|__|__|__|__|__|MM|MM|MM|MM|MM|__|__|\n" +
            "|__|__|HH|HH|HH|HH|HH|__|__|__|__|__|__|__|__|__|__|MM|MM|MM|MM|MM|__|__|\n" +
            "|__|__|HH|HH|HH|HH|HH|__|__|__|__|GC|GC|__|__|__|AM|MD|MM|MM|MM|MM|__|__|\n" +
            "|__|__|HH|HH|HH|HD|HH|__|__|__|__|GC|GC|__|__|__|__|MM|MM|MM|MD|MM|__|__|\n" +
            "|__|__|__|__|__|SH|__|__|__|__|__|__|__|__|__|__|__|__|__|__|SM|__|__|__|\n" +
            "|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|\n" +
            "|__|__|__|__|__|__|__|__|__|__|__|__|WV|__|__|__|__|__|__|__|__|__|__|__|\n" +
            "|__|__|__|__|__|__|__|__|__|VC|VC|VC|VD|VC|VC|__|__|__|__|__|__|__|__|__|\n" +
            "|__|__|__|__|__|GC|GC|__|__|VC|VC|VC|VC|VC|VD|DV|__|GC|GC|__|__|__|__|__|\n" +
            "|__|__|__|__|__|GC|GC|__|AV|VD|VC|VC|VC|VC|VC|__|__|GC|GC|__|__|__|__|__|\n" +
            "|__|__|__|__|__|__|__|__|__|VC|VC|VD|VC|VC|VC|__|__|__|__|__|__|__|__|__|\n" +
            "|__|__|__|__|__|__|__|__|__|__|__|SV|__|__|__|__|__|__|__|__|__|__|__|__|\n" +
            "|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|\n" +
            "|__|__|__|WC|__|__|__|__|__|__|__|__|__|__|__|__|__|__|WP|__|__|__|__|__|\n" +
            "|__|__|CC|CD|CC|CC|CC|__|__|__|__|GC|GC|__|__|__|__|PP|PD|PP|PP|PP|__|__|\n" +
            "|__|__|CC|CC|CC|CC|CD|DC|__|__|__|GC|GC|__|__|__|__|PP|PP|PP|PP|PP|__|__|\n" +
            "|__|__|CC|CC|CC|CC|CC|__|__|__|__|__|__|__|__|__|__|PP|PP|PP|PP|PP|__|__|\n" +
            "|__|__|CC|CC|CC|CC|CC|__|__|__|__|__|__|__|__|__|AP|PD|PP|PP|PP|PP|__|__|\n" +
            "|__|__|CC|CC|CC|CC|CC|__|__|__|__|__|__|__|__|__|__|PP|PP|PP|PP|PP|__|__|\n" +
            "|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|\n" +
            "|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|\n";
    // @formatter:on


    public Board(int width, int height){
        cells = new Cell[width][height];
        //Add all the estates to this board with lists of indexes to free storage cells
        estates.put("Haunted Door", new Estate("Haunted House", new ArrayList<>(Arrays.asList(6, 7, 8, 11, 12, 13, 16, 17, 18))));
        estates.put("Manic Door", new Estate("Manic Manor", new ArrayList<>(Arrays.asList(6, 7, 8, 11, 12, 13, 16, 17, 18))));
        estates.put("Peril Door", new Estate("Peril Palace", new ArrayList<>(Arrays.asList(6, 7, 8, 11, 12, 13, 16, 17, 18))));
        estates.put("Calamity Door", new Estate("Calamity Castle", new ArrayList<>(Arrays.asList(6, 7, 8, 11, 12, 13, 16, 17, 18))));
        estates.put("Villa Door", new Estate("Villa Celia", new ArrayList<>(Arrays.asList(7, 8, 9, 10, 13, 14, 15, 16, 17))));
    }

    /**
     * Redraw the estates to update objects inside
     */
    public void redrawEstates(){
        for(Map.Entry<String, Estate> mp : estates.entrySet()){
            mp.getValue().redrawEstate(this);
        }
    }

    /**
     * This method sets up the board before the game starts
     */
    public void setup() {
        Scanner sc = new Scanner(boardCells).useDelimiter("\\|");

        int row = 0;
        int col = 0;

        //The switch checks for the cell type and add it to the 2d array that makes up the baord
        while(sc.hasNext()){
            String next = sc.next();
            switch(next) {
                case "__":
                    cells[row][col++] = new FreeCell(row, col);
                    break;
                case "GC":
                    cells[row][col++] = new GreyCell(row, col);
                    break;
                case "CC":
                    EstateCell cc = new EstateCell(row, col,"Calamity", "Castle", false);

                    cells[row][col++] = cc;
                    estates.get("Calamity Door").addCell(cc);
                    break;
                case "CD":
                    EstateCell cd = new EstateCell(row, col,"Calamity", "Door", true);

                    cells[row][col++] = cd;
                    estates.get("Calamity Door").addCell(cd);
                    break;
                case "PP":
                    EstateCell pp = new EstateCell(row, col,"Peril", "Palace", false);

                    cells[row][col++] = pp;
                    estates.get("Peril Door").addCell(pp);
                    break;
                case "PD":
                    EstateCell pd = new EstateCell(row, col,"Peril", "Door", true);

                    cells[row][col++] = pd;
                    estates.get("Peril Door").addCell(pd);
                    break;
                case "MM":
                    EstateCell mm = new EstateCell(row, col,"Manic", "Manor", false);

                    cells[row][col++] = mm;
                    estates.get("Manic Door").addCell(mm);
                    break;
                case "MD":
                    EstateCell md = new EstateCell(row, col,"Manic", "Door", true);

                    cells[row][col++] = md;
                    estates.get("Manic Door").addCell(md);
                    break;
                case "HH":
                    EstateCell hh = new EstateCell(row, col,"Haunted", "House", false);

                    cells[row][col++] = hh;
                    estates.get("Haunted Door").addCell(hh);
                    break;
                case "HD":
                    EstateCell hd = new EstateCell(row, col,"Haunted", "Door", true);

                    cells[row][col++] = hd;
                    estates.get("Haunted Door").addCell(hd);
                    break;
                case "VC":
                    EstateCell vc = new EstateCell(row, col,"Villa", "Celia", false);

                    cells[row][col++] = vc;
                    estates.get("Villa Door").addCell(vc);
                    break;
                case "VD":
                    EstateCell vd = new EstateCell(row, col,"Villa", "Door", true);

                    cells[row][col++] = vd;
                    estates.get("Villa Door").addCell(vd);
                    break;
                default:

                    //If the cell is an exit door cell this method add it to the correct estate as well as the exit direction
                    if(EXITCELLPATTERN.matcher(next).find()){
                        Cell exitCell = new FreeCell(row, col);
                        cells[row][col++] = exitCell;

                        String key = next.substring(1, 2);
                        String direction = next.substring(0, 1);

                        switch (key) {
                            case "V":
                                estates.get("Villa Door").addExitCell(exitCell, direction);
                                break;
                            case "H":
                                estates.get("Haunted Door").addExitCell(exitCell, direction);
                                break;
                            case "P":
                                estates.get("Peril Door").addExitCell(exitCell, direction);
                                break;
                            case "M":
                                estates.get("Manic Door").addExitCell(exitCell, direction);
                                break;
                            case "C":
                                estates.get("Calamity Door").addExitCell(exitCell, direction);
                                break;
                            default:
                        }
                    } else {
                        row++;
                        col = 0;
                    }
            }
        }
    }

    /**
     * Redraw a single cell at a given position
     * @param row the row to be redrawn
     * @param col the col to be redrawn
     * @param c the cell to to be drawn in that destination
     */
    public void redrawCell(int row, int col, Cell c){
        cells[row][col] = c;
    }

    /*
     * Getters and setters
     */

    public Cell[][] getCells(){
        return cells;
    }

    public void setCells(Cell[][] cells){
        this.cells = cells;
    }

    public void setPlayer(Player p){
        cells[p.getRow()][p.getCol()] = new PlayerCell(p.getRow(), p.getCol(), p.getName());
    }

    public Estate getEstate(String name){
        return estates.get(name);
    }

    public Cell getCell(int row, int col){
        return cells[row][col];
    }

    public Map<String, Estate> getEstates(){
        return estates;
    }


    /**
     * This toString is for drawing the board onto the text pane for the user
     * It simply iterates the 2d array and uses each objects toString to draw the correct cell visualizations
     * @return The board as a big string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Cell[] c1 : cells) {

            for(Cell c2 : c1){
                sb.append("|");
                sb.append(c2.toString());
            }
            sb.append("|");
            sb.append("\n");
        }
        return sb.toString();
    }
}