import Cells.*;

import java.util.*;
import java.util.Random;

/***
 * The player class holds all the information about the player and its status in the game. The object also has all the methods for
 * moving the player around the board, as well as going in and out of estate objects
 */
public class Player extends Move implements Cloneable {
    private boolean turn = false;
    private Estate estateIn = null;
    private String name;
    private int row;
    private int col;
    private Random dice1 = new Random();
    private Random dice2 = new Random();
    private final int UPPERBOUND = 6;
    private int steps = 0;
    private boolean rollStatus = false;
    private boolean isOut = false;
    private boolean hasWon = false;
    private boolean hasGuessed = false;

    private ArrayList<Card> guesses;
    private List<Card> hand;
    private List<Cell> visited;

    public Player(String name, int row, int col) {
        super(row, col);
        this.name = name;
        this.row = row;
        this.col = col;
        hand = new ArrayList<>();
        guesses = new ArrayList<>();
        this.visited = new ArrayList<>();
    }

    /**
     * Roll the dice for this player object using the upperbound const
     */
    public void roll() {
        if (turn) {
            int d1 = dice1.nextInt(UPPERBOUND) + 1;
            int d2 = dice2.nextInt(UPPERBOUND) + 1;
            System.out.println("You rolled a " +d1 + " and a "+ d2);
            steps = d1 + d2;

        }
    }

    /**
     * Simple method for declaring this player object as the winner
     *
     * @param b boolean true or false if this player object has won
     */
    public void setHasWon(boolean b) {
        hasWon = b;
    }

    /**
     * Display the players hand to the system out
     */
    public void printHand() {
        System.out.println(this.name + "'s" + " current Hand: ");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println(i + ": " + hand.get(i).name);
        }
    }

    /**
     * Method for cloning the player
     *
     * @return return a deep clone of this player object
     */
    @Override
    public Player clone() {
        Player p = new Player(this.name, this.row, this.col);
        for (Card c : this.hand) {
            p.hand.add(c.clone());
        }
        return p;
    }

    /**
     * after checking the move is valid move the player on the given board
     *
     * @param b         the current board
     * @param direction the direction the player is moving
     */
    public void move(Board b, String direction) {
        if (isValid(b, direction)) {


            Cell[][] cells = b.getCells();
            Cell playerCell = cells[row][col];
            cells[row][col] = new FreeCell(row, col);

            //Add the current cell to a visited arraylist for checking later
            visited.add(b.getCell(row, col));

            switch (direction) {
                case "W":
                    cells[row - 1][col] = playerCell;
                    row = row - 1;
                    b.setCells(cells);
                    break;

                case "A":
                    cells[row][col - 1] = playerCell;
                    col = col - 1;
                    b.setCells(cells);
                    break;

                case "S":
                    cells[row + 1][col] = playerCell;
                    row = row + 1;
                    b.setCells(cells);
                    break;

                case "D":
                    cells[row][col + 1] = playerCell;
                    col = col + 1;
                    b.setCells(cells);
                    break;

                default:
                    break;
            }

            b.redrawEstates();
        }else if (this.getEstateIn() != null) {
            isValidEstate(b,direction);

        } else {
            System.out.println("Move is not valid");
        }
    }

    /**
     * Checks which direction the player wants to move and if the estate contains a door and if the the cell hasn't not already been visited this turn
     * If direction is correct and cell unvisited player is removed from the state and redrawn outside the door
     *
     * @param b         the current board
     * @param direction the direction the player wants to move
     *
     */

    public void isValidEstate(Board b, String direction){
        Cell[][] c = b.getCells();
        //Check for a valid exit

            Cell newPos = estateIn.containsExit(direction);
            if(newPos != null) {
                //Move the player to an exit point and remove them from the estate
                if (checkVisited(c[newPos.getRow()][newPos.getCol()])) {
                    estateIn.removePlayersInEstate(this);
                    c[newPos.getRow()][newPos.getCol()] = new PlayerCell(newPos.getRow(), newPos.getCol(), this.name);
                    b.redrawEstates();
                    this.row = newPos.getRow();
                    this.col = newPos.getCol();
                    steps--;
                    estateIn = null;
                }else{
                    System.out.println("Move is not valid");
                }

        }else{
                System.out.println("Move is not valid");
            }
    }

    /**
     * This method is for checking if a desired player move is a valid move or not
     * It also checks if the player is trying to enter an estate and reacts accordingly
     *
     * @param b         the current board
     * @param direction the direction the player wants to move
     * @return true or false if this is a vailid move
     */
    @Override
    public boolean isValid(Board b, String direction) {

        int tempRow = row;
        int tempCol = col;

        switch (direction) {
            case "W":
                tempRow--;
                break;
            case "A":
                tempCol--;
                break;
            case "S":
                tempRow++;
                break;
            case "D":
                tempCol++;
                break;
            default:
                break;
        }

        Cell[][] c = b.getCells();

        //Check if player is currently in an estate and trying to leaving in a given direction
        if (estateIn != null) {
            return false;
        }

        //Make sure the move doesnt go out of bounds or is not a visited cell
        if (outOfBounds(tempRow, tempCol) || !checkVisited(c[tempRow][tempCol])) {
            return false;
        }


        if (c[tempRow][tempCol] != null) {
            if (c[tempRow][tempCol] instanceof FreeCell) {
                steps--;
                return true;
            }
            //If the player is trying to enter an estate make sure its entering a door cell and add it to that estate
            if (c[tempRow][tempCol] instanceof EstateCell) {
                EstateCell ec = (EstateCell) c[tempRow][tempCol];
                if (ec.isDoor()) {
                    estateIn = b.getEstate(ec.getName());
                    estateIn.addPlayersInEstate(this, (PlayerCell) c[row][col]);
                    steps--;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check weather the desired move is out of the board boundaries
     *
     * @param tempRow the temporary column to be checked
     * @param tempCol the temporary row to be check
     * @return true or false boolean to signify weather the move is out of bounds
     */
    private boolean outOfBounds(int tempRow, int tempCol) {
        return tempCol > 23 || tempRow > 23 || tempCol < 0 || tempRow < 0;
    }


    /**
     * Check weather a player is trying to re-visit a cell
     *
     * @param c the cell to be check
     * @return true or false if the player has visited the given cell
     */
    public boolean checkVisited(Cell c) {
        return !visited.contains(c);
    }

    /**
     * Adds a guess card to this player
     *
     * @param c the guess card to add
     */
    public void addGuess(Card c) {
        this.guesses.add(c);
    }

    /**
     * Clear the steps for this player
     */
    public void clearSteps(){
        this.steps = 0;
    }

    /**
     * Clear the guess cards for this player
     */
    public void clearGuess() {
        this.guesses.clear();
    }

    /**
     * Add a card to this players hand
     *
     * @param card the card to be added
     */
    public void addHand(Card card) {
        this.hand.add(card);
    }

    /**
     * Clear visited cells for when a player has finished there turn
     */
    public void clearVisited() {
        visited.clear();
    }

    /*
     * Getter and setters for this player object
     */



    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setTurn(boolean aTurn) {
        this.turn = aTurn;
    }

    public boolean getTurn() {
        return this.turn;
    }

    public List<Card> getGuess() {
        return this.guesses;
    }

    public List<Card> getHand() {
        return this.hand;
    }

    public boolean getHasWon() {
        return hasWon;
    }

    public String getName() {
        return this.name;
    }

    public int getSteps() {
        return this.steps;
    }

    public void setIsout(boolean b) {
        isOut = b;
    }

    public boolean getIsOut() {
        return isOut;
    }

    public boolean getRollStatus() {
        return rollStatus;
    }

    public void setRollStatus(boolean b) {
        rollStatus = b;
    }

    public boolean getGuessStatus() {
        return hasGuessed;
    }

    public void setGuessStatus(boolean b) {
        hasGuessed = b;
    }

    public Estate getEstateIn(){
        return this.estateIn;
    }

    public String getEstateInString() {
        if (estateIn == null) {
            return "null";
        }
        return estateIn.getEstateName();
    }

    /**
     * toString method for getting the player initial two letters of there name
     *
     * @return first two letters of this players name
     */
    @Override
    public String toString() {

        return name.substring(0, 2);
    }

}