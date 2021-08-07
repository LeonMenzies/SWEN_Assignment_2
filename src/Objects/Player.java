package Objects;

import Cards.*;
import Cells.*;
import Gui.BoardCanvas;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Random;

/***
 * The player class holds all the information about the player and its status in the game. The object also has all the methods for
 * moving the player around the board, as well as going in and out of estate objects
 */
public class Player extends Move implements Cloneable, Movable {
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

    private Image cellImage;

    private ArrayList<Card> guesses;
    private List<Card> hand;
    private List<Cell> visited;

    public Player(String name, int row, int col, Image cellImage) {
        super(row, col);
        this.cellImage = cellImage;
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
            System.out.println("You rolled a " + d1 + " and a " + d2);
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
        Player p = new Player(this.name, this.row, this.col, this.cellImage);
        for (Card c : this.hand) {
            p.hand.add(c.clone());
        }
        return p;
    }


    /**
     * after checking the move is valid move the player on the given board
     *
     * @param b the current board
     */
    public boolean move(Board b, Cell selected) {

        //Move player out of estate
        if (this.inEstate()) {
            if (estateIn.containsExit(selected) && !visited.contains(selected)) {

                this.row = selected.getRow();
                this.col = selected.getCol();

                estateIn.removePlayersInEstate(this);
                estateIn = null;
                b.addPlayer(this);

                visited.add(b.getCell(row, col));
                return true;

            }
        }

        //Move player
        if (isValid(b, selected)) {
            this.row = selected.getRow();
            this.col = selected.getCol();
            visited.add(b.getCell(row, col));
            return true;
        }
        return false;

    }

    /**
     * This method is for checking if a desired player move is a valid move or not
     * It also checks if the player is trying to enter an estate and reacts accordingly
     *
     * @param b the current board
     * @return true or false if this is a vailid move
     */
    @Override
    public boolean isValid(Board b, Cell selected) {

        if (!visited.contains(selected) && selected instanceof FreeCell) {
            if (Math.abs(this.row - selected.getRow()) + Math.abs(this.col - selected.getCol()) > 1) {
                return false;
            }
            return true;
        } else if (selected instanceof EstateCell) {
            EstateCell ec = (EstateCell) selected;
            if (ec.isDoor()) {
                estateIn = b.getEstate(ec.getName());
                estateIn.addPlayersInEstate(this);
                b.removePlayer(this);
                steps--;
                return true;
            }
        }

        return false;

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
    public void clearSteps() {
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

    public Estate getEstateIn() {
        return this.estateIn;
    }

    public Image getCellImage() {
        return cellImage;
    }

    public boolean inEstate() {
        return estateIn != null;
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