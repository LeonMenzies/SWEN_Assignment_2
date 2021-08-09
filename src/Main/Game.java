package Main;

import Cards.Card;
import Cards.CharacterCard;
import Cards.EstateCard;
import Cards.WeaponCard;
import Gui.MurderM;
import Objects.Board;
import Objects.Estate;
import Objects.Player;
import Objects.Weapon;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Game{
    ArrayList<Player> players = new ArrayList<>();
    Queue<Integer> playerOrder = new ArrayDeque<>();
    ArrayList<Player> tempPlayers = new ArrayList<>();
    ArrayList<Card> deck = new ArrayList<>();
    ArrayList<Card> tempDeck = new ArrayList<>();
    ArrayList<Card> guessDeck = new ArrayList<>();
    ArrayList<Card> circumstance = new ArrayList<>();
    ArrayList<Card> refuteCards = new ArrayList<>();
    CharacterCard who = null;
    EstateCard where = null;
    WeaponCard what = null;
    boolean gameWon = false;

    int playerTurn = 0;
    private boolean gameStarted = false;
    private boolean gameOver = false;

    Player currentPlayer = null;
    private final MurderM gui;



    private final Board board;




    public Game(MurderM gui) {
        this.board = gui.getBoard();
        this.gui = gui;
    }

    public Player getCurrent(){
        return currentPlayer;
    }

    public boolean getGameStatus(){
        return !gameOver && !gameWon;
    }



    public void playGame(){
        if(!gameWon && !gameOver) {
            Integer pt = playerOrder.poll();
            if(pt != null) {
                playerTurn = pt;
                currentPlayer = players.get(playerTurn);
                currentPlayer.setTurn(true);
                gui.notifyObservers();
                currentPlayer.setRollStatus(false);
                currentPlayer.setGuessStatus(false);
                gui.displayPlayer(currentPlayer.getName());
            }
        }else if(gameWon && !gameOver){
            gui.displayOkOption(currentPlayer.getName() + " has won!", "Winner");
        }else if(!gameWon){
            gui.displayOkOption("No one can guess game is over", "Game Over");
        }
    }

    public void setGameStarted(boolean b){
        gameStarted = b;
    }

    /**
     * Objects.Player is making final guess checks to see if they have won or not
     *
     * @param guess list of the current guess
     * @return true or false if all the cards match
     */

    public boolean checkWin(List<Card> guess) {
        int count = 0;
        //goes through the circumstance and compares them to the players guess if count equals 3 then all cards matched
        for (Card c : circumstance) {
            for (Card c1 : guess) {
                if (c.getName().equals(c1.getName())) {
                    count++;
                }
            }
        }

        return count == 3;
    }



    public void endTurn(){

        if(gameStarted && !gameWon && !gameOver) {
            int result = gui.displayOkOption("Are you sure want to end your turn?", "End Turn");
            if(result == 0) {
                if(currentPlayer.getSteps() == 0 || !currentPlayer.getEstateInString().equals("null")) {
                    if (!currentPlayer.getIsOut()) {
                        currentPlayer.setTurn(false);
                        playerOrder.offer(playerTurn);
                    }
                    playGame();
                }else{
                    gui.errorMessage("You must be out of steps or in an estate to end your turn", "End turn Error");
                }
            }

        }
    }

    public void roll(){
        if(gameStarted && !gameWon && !gameOver) {
            if (!currentPlayer.getRollStatus()) {
                currentPlayer.roll();
                currentPlayer.setRollStatus(true);
            } else {
                gui.errorMessage("You have already rolled", "Roll Error");
            }
        }

    }
    public boolean checkGameOver(){
        int count = 0;
        for(Player p : players){
            if(p.getIsOut()){
                count++;
            }
        }
        return players.size() == count;
    }

    public void makeGuess() {
        if(gameStarted && !gameWon && !gameOver) {
            guessDeck.clear();
            refuteCards.clear();
            if (!currentPlayer.getGuessStatus() && !currentPlayer.getEstateInString().equals("null")) {
                guessDeck = gui.makeGuess(tempDeck);
                if (guessDeck.size() > 0) {
                    currentPlayer.setGuessStatus(true);
                    refute(guessDeck);

                }

            } else {
                gui.errorMessage("You have already made a guess or need to be in an estate", "Guess Error");
            }
        }

    }

    public void finalGuess(){
        if(gameStarted && !gameWon && !gameOver){
            guessDeck.clear();
            if(!currentPlayer.getGuessStatus()){
                guessDeck = gui.makeGuess(tempDeck);
                if(guessDeck.size() > 0){
                   gameWon = checkWin(guessDeck);
                   if(gameWon){
                       playGame();
                   }else{
                       currentPlayer.setIsout(true);
                       gameOver = checkGameOver();
                       if(gameOver){
                           playGame();
                       }
                       gui.displayMessage(currentPlayer.getName() + " you are out you can still refute");
                   }

                }
            }else{
                gui.errorMessage("You have already made a guess", "Guess Error");
            }

        }
    }

    public void refute(ArrayList<Card> guess) {
        refuteOrder(currentPlayer);
        //display guess

        for (Player p : tempPlayers) {
          Card c = gui.refute(guess,p);
          if(c != null){
              refuteCards.add(c);
          }
        }

    }



    /**
     * Checks to see if the card refute is a legit refute
     *
     * @param guess list of the current guess
     * @param c     the card the player is putting in as the refute
     * @return true or false if the the card is an actual refute or not
     */
    public boolean isARefute(List<Card> guess, Card c) {
        //compares all the guess to the suggested card
        for (Card c1 : guess) {
            if (c1.getName().equals(c.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if player is telling truth when saying cant refute
     *
     * @param guess list of the current guess
     * @param hand  list of the players hand
     * @return true or false if the play can refute or not
     */
    public boolean checkRefute(List<Card> guess, List<Card> hand) {
        //compares all the cards in the guess to the players hand
        for (Card c1 : guess) {
            for (Card c2 : hand) {
                if (c1.getName().equals(c2.getName())) {
                    return true;
                }
            }
        }
        return false;
    }




    /**
     * Objects.Move the the weapon and player the on the board into the estate that the player is making the guess in
     *
     * @param player player making the guess
     * @param gWhat  current weapon card guess
     * @param gWho   current player card guess
     */

    public void moveCharacters(Player player, WeaponCard gWhat, CharacterCard gWho) {


    }

    /**
     * Make sure the order to refute is correct depending on who is making the guess
     *
     * @param p player making the guess
     */
    public void refuteOrder(Player p) {
        //clears previous order and clones the players in there original order into the array putting aside the player
        //who is making the guess
        tempPlayers.clear();
        Player guesser = null;
        for (Player p1 : players) {
            if (p1.getName().equals(p.getName())) {
                guesser = p1;
                tempPlayers.add(guesser);
            } else {
                tempPlayers.add(p1.clone());
            }
        }
        //moves the ones in front of the guesser to the back of the array then removes them from the front
        int i = tempPlayers.indexOf(guesser);

        List<Player> toAdd = new ArrayList<>();
        for (int j = i - 1; j >= 0; j--) {
            Player temp = tempPlayers.get(j);
            tempPlayers.remove(j);
            toAdd.add(temp);

        }
        Collections.reverse(toAdd);
        tempPlayers.addAll(toAdd);
        //finally removes the guesser from the order
        tempPlayers.remove(guesser);

    }

    /**
     * Generate starting order for the game randomly chosen start but still follow same order as refute goes in
     *
     * @param p starting player
     */
    public void generateStartingOrder(Player p) {
        int i = players.indexOf(p);
        //index of starting player and players in front of that player are removed from the front and added to a separate array
        List<Player> toAdd = new ArrayList<>();
        for (int j = i - 1; j >= 0; j--) {
            Player temp = players.get(j);
            players.remove(j);
            toAdd.add(temp);

        }
        //reserve the order as they are added in the wrong way then the new array is added to the end of the players array
        Collections.reverse(toAdd);

        players.addAll(toAdd);

    }


    /**
     * Method to set Up the right amount of players in the game either 3 or 4
     * Right amount of players are then added to the player Array for the game
     */
    public void playerSetUp(int numPlayers) {
        //scans in a string from the console


        //players are then added to the array depending on the amount
        players.add(new Player("Lucilla", 1, 11, board.getCellImages().get("Lucilla")));
        players.add(new Player("Bert", 9, 1, board.getCellImages().get("Bert")));
        players.add(new Player("Malina", 22, 9, board.getCellImages().get("Malina")));
        playerOrder.add(0);
        playerOrder.add(1);
        playerOrder.add(2);

        //4 player gets added in if necessary
        if (numPlayers == 4) {
            playerOrder.add(3);
            players.add(new Player("Percy", 14, 22, board.getCellImages().get("Percy")));
        }


        for (Player p : players) {
            board.addPlayer(p);
        }


    }

    public void setUp(int playerSize){
        playerSetUp(playerSize);
        setUpDeck();
        generateMurder();
        for(Card c : circumstance){
            System.out.println(c.getName());
        }
        dealCards();
        Random rand = new Random();
        int i = rand.nextInt(players.size());
        Player p = players.get(i);
        generateStartingOrder(p);
        weaponSetup();


    }


    /**
     * Generates the Weapons then randomly distributes them through out the estates
     */

    public void weaponSetup() {
        ArrayList<Weapon> wpList = new ArrayList<>();
        try {
            wpList.add(new Weapon("Broom", 0, 0, null, ImageIO.read(new File("src/resources/weapon_broom.png"))));
            wpList.add(new Weapon("Scissors", 0, 0, null, ImageIO.read(new File("src/resources/weapon_scissors.png"))));
            wpList.add(new Weapon("Knife", 0, 0, null, ImageIO.read(new File("src/resources/weapon_knife.png"))));
            wpList.add(new Weapon("Shovel", 0, 0, null, ImageIO.read(new File("src/resources/weapon_shovel.png"))));
            wpList.add(new Weapon("iPad", 0, 0, null, ImageIO.read(new File("src/resources/weapon_ipad.png"))));
        } catch (IOException e) {
            System.out.println("Weapon image not found");
        }
        Collections.shuffle(wpList);

        int count = 0;
        for (Map.Entry<String, Estate> e : board.getEstates().entrySet()) {
            e.getValue().addWeaponInEstate(wpList.get(count));
            wpList.get(count).setEstate(e.getValue());
            count++;
        }
        board.addWeapons(wpList);
    }

    /**
     * Method to add all the cards to the game of the correct type
     * shuffles the array after the cards have been added
     */
    public void setUpDeck() {
        this.deck.add(new CharacterCard("Bert"));
        this.deck.add(new CharacterCard("Percy"));
        this.deck.add(new CharacterCard("Lucilla"));
        this.deck.add(new CharacterCard("Malina"));
        this.deck.add(new EstateCard("Haunted House"));
        this.deck.add(new EstateCard("Manic Manor"));
        this.deck.add(new EstateCard("Villa Celia"));
        this.deck.add(new EstateCard("Calamity Castle"));
        this.deck.add(new EstateCard("Peril Palace"));
        this.deck.add(new WeaponCard("Broom"));
        this.deck.add(new WeaponCard("Scissors"));
        this.deck.add(new WeaponCard("Knife"));
        this.deck.add(new WeaponCard("Shovel"));
        this.deck.add(new WeaponCard("iPad"));

        //clones the cards to a temp deck for making guesses
        for (Card c : deck) {
            tempDeck.add(c.clone());
        }
        Collections.shuffle(this.deck);
    }

    /**
     * Method to generate the murder circumstances one of each type of card is selected at random
     */
    public void generateMurder() {
        //if array size is 3 enough cards has been selected
        if (circumstance.size() == 3) {
            return;
        }
        //grabs a random card from the deck
        Random rand = new Random();

        Card c = this.deck.get(rand.nextInt(this.deck.size()));

        //checks what type of card it is if that is currently null then that card is the new circumstance
        //either generate murder is called again until all three cards are chosen
        //cards are then removed from the deck
        if (c instanceof CharacterCard) {
            if (this.who == null) {
                this.who = (CharacterCard) c;
                this.circumstance.add(this.who);
                this.deck.remove(this.who);
            }
            generateMurder();
        }
        if (c instanceof EstateCard) {
            if (this.where == null) {
                this.where = (EstateCard) c;
                this.circumstance.add(this.where);
                this.deck.remove(this.where);
            }
            generateMurder();
        }
        if (c instanceof WeaponCard) {
            if (this.what == null) {
                this.what = (WeaponCard) c;
                this.circumstance.add(this.what);
                this.deck.remove(this.what);
            }
            generateMurder();
        }

    }

    /**
     * Deals the remaining cards (after the murder circumstance as been selected)
     * out randomly amongst the players until there is none left
     */
    public void dealCards() {

        //until the deck is empty loops through the players until there is none left
        while (!this.deck.isEmpty()) {
            for (Player p : this.players) {
                if (this.deck.isEmpty()) {
                    break;
                }
                Random rand = new Random();
                Card c = this.deck.get(rand.nextInt(this.deck.size()));
                p.addHand(c);
                this.deck.remove(c);
            }
        }
    }




}