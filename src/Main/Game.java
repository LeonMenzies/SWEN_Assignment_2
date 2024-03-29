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
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Game{
    ArrayList<Weapon> wpList = new ArrayList<>();

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


    /**
     *  Gets the current status of the game
     *
     * @return true if !gameOver and !gameWon
     */
    public boolean getGameStatus(){
        return !gameOver && !gameWon;
    }


    /**
     *  Play game goes through and checkes the status of the game and then decides what action to take.
     *  If game is over will display no one has won, If game is won will display who has won
     *  otherwise the next players turn gets started
     */
    public void playGame(){
        if(!gameWon && !gameOver) {
            //pulls the number from the que
            Integer pt = playerOrder.poll();
            if(pt != null) {
                //sets the current player to that person in the array and clears all there variables for their turn
                playerTurn = pt;
                currentPlayer = players.get(playerTurn);
                currentPlayer.setTurn(true);
                gui.notifyObservers();
                currentPlayer.clearVisted();
                currentPlayer.setRollStatus(false);
                currentPlayer.setGuessStatus(false);
                //updates the gui with who's turn then displays all there information such as hand, steps
                gui.displayPlayer(currentPlayer.getActualName());
                gui.displaySteps();
                gui.setCurrentPlayer(currentPlayer.getActualName(),currentPlayer.getName());
                gui.displayHand(currentPlayer.getHand());

            }
            //updates the gui with who has won
        }else if(gameWon && !gameOver){
            gui.displayOkOption(currentPlayer.getActualName() + " has won!", "Winner");
        }else if(!gameWon){
            gui.displayOkOption("No one can guess game is over", "Game Over");
        }
    }


    /**
     *  sets the gameStarted boolean variable
     *
     * @param b boolean true or false
     */
    public void setGameStarted(boolean b){
        gameStarted = b;
    }

    /**
     * Player is making final guess checks to see if they have won or not
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


    /**
     *Ends the current players turn checks to see if they are out of steps on in an estate first
     */

    public void endTurn(){
        //checks to see what state the game is in
        if(gameStarted && !gameWon && !gameOver) {
            int result = gui.displayOkOption("Are you sure want to end your turn?", "End Turn");
            if(result == 0) {
                //if the players steps are 0 and they are in estate checks they arent out if they are out there number doesnt get put back into the que
                if(currentPlayer.getSteps() == 0 || !currentPlayer.getEstateInString().equals("null")) {
                    if (!currentPlayer.getIsOut()) {
                        currentPlayer.setTurn(false);
                        playerOrder.offer(playerTurn);
                    }
                    //resets the display for the next play and calls the playGame method
                    gui.resetDisplay();
                    playGame();
                }else{
                    gui.errorMessage("You must be out of steps or in an estate to end your turn", "End turn Error");
                }
            }

        }
    }


    /**
     * Rolls the dice for the player and sets there variables then displays there steps
     */
    public void roll(){
        //checks current status of the game
        if(gameStarted && !gameWon && !gameOver) {
            //if they have rolled rolls the dice and displays for player
            if (!currentPlayer.getRollStatus()) {
                currentPlayer.roll();
                currentPlayer.setRollStatus(true);
                gui.displaySteps();

            } else {
                gui.errorMessage("You have already rolled", "Roll Error");
            }
        }

    }

    /**
     * Method that goes through all the current players in the game and checks if they are all out
     *
     * @return true if all out otherwise false
     */
    public boolean checkGameOver(){
        int count = 0;
        for(Player p : players){
            if(p.getIsOut()){
                count++;
            }
        }
        return players.size() == count;
    }


    /**
     * Guess clears the guess deck and refute deck. Makes a new guess panel with the temp deck then moves the selected characters and weapons into the right estate
     */
    public void makeGuess() {
        //checks current game state
        if(gameStarted && !gameWon && !gameOver) {
            guessDeck.clear();
            refuteCards.clear();
            if (!currentPlayer.getGuessStatus() && !currentPlayer.getEstateInString().equals("null")) {
                guessDeck = gui.makeGuess(tempDeck);
                if (guessDeck.size() > 0) {
                    //moves the characters and weapons then disable the current players hand so other cant see and displays there guess
                    moveCharacters();
                    currentPlayer.setGuessStatus(true);
                    gui.setHandDisplay(false);
                    gui.displayGuess(guessDeck);
                    //calls the refute method with the new guess deck
                    refute(guessDeck);

                }

            } else {
                gui.errorMessage("You have already made a guess or need to be in an estate", "Guess Error");
            }
        }

    }


    /**
     * Player makes a final guess from a list of cards. Then checks if they have won or are out
     */
    public void finalGuess(){
        if(gameStarted && !gameWon && !gameOver){
            guessDeck.clear();
            if(!currentPlayer.getGuessStatus()){
                guessDeck = gui.makeGuess(tempDeck);
                if(guessDeck.size() > 0){
                    //checks they have won and sets gameWon
                    gameWon = checkWin(guessDeck);
                    //if game not won player is out and checks if the all players are out
                    if (!gameWon) {
                        gui.displayMessage(currentPlayer.getActualName() + " you are out you can still refute");
                        currentPlayer.setIsout(true);
                        gameOver = checkGameOver();
                    }
                    gui.resetDisplay();
                    playGame();

                   }

                }
            }else{
                gui.errorMessage("You have already made a guess", "Guess Error");
            }

        }


    /**
     * Goes through every player expect the one making the guess and prompts them to make a refute
     */
    public void refute(ArrayList<Card> guess) {
        //gets the right order for disputing
        refuteOrder(currentPlayer);

        //checks if there refute wasnt null and adds it to the list of refutes
        for (Player p : tempPlayers) {
          Card c = gui.refute(guess,p);
          if(c != null){
              refuteCards.add(c);
          }
        }
        //prompts for player guessing to get the tablet back then displays there hand again and the refute
        gui.displayOkOption("Please hand the tablet back to "+ currentPlayer.getActualName(), "Refute");
        gui.displayRefute(refuteCards);
        gui.setHandDisplay(true);

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
     * OMove the the weapon and player the on the board into the estate that the player is making the guess in
     *
     */

    public void moveCharacters() {
        Card weaponC = null;
        Card characterC = null;
        for (Card c : guessDeck) {
            if (c instanceof WeaponCard) {
                weaponC = c;
            }
            if (c instanceof CharacterCard) {
                characterC = c;
            }
        }

        Player pl;
        Estate we;
        Estate e = currentPlayer.getEstateIn();

        //Goes through the list of weapons finds the one that match's that of the guess removes it from its current estate into the new one
        for (Weapon w1 : wpList) {
            assert weaponC != null;
            if (w1.getWepName().equals(weaponC.getName())) {

                we = w1.getEstate();
                if (!we.getEstateName().equals(e.getEstateName())) {
                    we.removeWeaponInEstate(w1);
                    e.addWeaponInEstate(w1);
                }
            }
        }

        for (Player p1 : players) {
            assert characterC != null;
            if (p1.getName().equals(characterC.getName())) {
                pl = p1;

                if (pl.getEstateIn() != null) {

                    Estate es = pl.getEstateIn();
                    if (!es.getEstateName().equals(e.getEstateName())) {
                        es.removePlayersInEstate(pl);
                        e.addPlayersInEstate(pl);
                    }

                }else{
                    e.addPlayersInEstate(pl);
                    board.removePlayer(pl);
                    pl.setEstateIn(e);
                }
            }
        }
        gui.notifyObservers();
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
    public void playerSetUp(String[] aNames, String[] cNames) {
        //scans in a string from the console
        HashMap<String, Image> bert_directions = new HashMap<>();
        HashMap<String, Image> lucilla_directions = new HashMap<>();
        HashMap<String, Image> malina_directions = new HashMap<>();
        HashMap<String, Image> percy_directions = new HashMap<>();

        try {
            bert_directions.put("up", ImageIO.read(new File("src/resources/player_be_up.png")));
            bert_directions.put("down", ImageIO.read(new File("src/resources/player_be_down.png")));
            bert_directions.put("left", ImageIO.read(new File("src/resources/player_be_left.png")));
            bert_directions.put("right", ImageIO.read(new File("src/resources/player_be_right.png")));

            lucilla_directions.put("up", ImageIO.read(new File("src/resources/player_lu_up.png")));
            lucilla_directions.put("down", ImageIO.read(new File("src/resources/player_lu_down.png")));
            lucilla_directions.put("left", ImageIO.read(new File("src/resources/player_lu_left.png")));
            lucilla_directions.put("right", ImageIO.read(new File("src/resources/player_lu_right.png")));

            malina_directions.put("up", ImageIO.read(new File("src/resources/player_ma_up.png")));
            malina_directions.put("down", ImageIO.read(new File("src/resources/player_ma_down.png")));
            malina_directions.put("left", ImageIO.read(new File("src/resources/player_ma_left.png")));
            malina_directions.put("right", ImageIO.read(new File("src/resources/player_ma_right.png")));

            percy_directions.put("up", ImageIO.read(new File("src/resources/player_pe_up.png")));
            percy_directions.put("down", ImageIO.read(new File("src/resources/player_pe_down.png")));
            percy_directions.put("left", ImageIO.read(new File("src/resources/player_pe_left.png")));
            percy_directions.put("right", ImageIO.read(new File("src/resources/player_pe_right.png")));

        }catch(IOException e){
            System.out.println("Image not found");
        }

        //players are added to the array based on what character they picked an there actual names
        for(int i = 0 ; i < aNames.length; i++){
            playerOrder.add(i);
            if(cNames[i].equals("Bert")){
                players.add(new Player("Bert", 9, 1, bert_directions,aNames[i], "right"));
            }
            if(cNames[i].equals("Percy")){
                players.add(new Player("Percy", 14, 22, percy_directions,aNames[i], "left"));
            }
            if(cNames[i].equals("Malina")){
                players.add(new Player("Malina", 22, 9, malina_directions,aNames[i], "up"));
            }
            if(cNames[i].equals("Lucilla")){
                players.add(new Player("Lucilla", 1, 11, lucilla_directions, aNames[i], "down"));
            }
        }



        for (Player p : players) {
            board.addPlayer(p);
        }


    }

    /**
     * Method to setUp the aspects of the game such as player names, all the cards and what the murder circumstance is
     *
     * @param aNames list of players actual names
     */


    public void setUp(String[] aNames,String[] cNames){
        playerSetUp(aNames,cNames);
        setUpDeck();
        generateMurder();

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
        try {
            this.deck.add(new CharacterCard("Bert", ImageIO.read(new File("src/resources/player_be_right.png"))));
            this.deck.add(new CharacterCard("Percy", ImageIO.read(new File("src/resources/player_pe_right.png"))));
            this.deck.add(new CharacterCard("Lucilla", ImageIO.read(new File("src/resources/player_lu_right.png"))));
            this.deck.add(new CharacterCard("Malina", ImageIO.read(new File("src/resources/player_ma_right.png"))));
            this.deck.add(new EstateCard("Haunted House", ImageIO.read(new File("src/resources/estate_haunted_house.png"))));
            this.deck.add(new EstateCard("Manic Manor", ImageIO.read(new File("src/resources/estate_manic_manor.png"))));
            this.deck.add(new EstateCard("Villa Celia", ImageIO.read(new File("src/resources/estate_villa_celia.png"))));
            this.deck.add(new EstateCard("Calamity Castle", ImageIO.read(new File("src/resources/estate_calamity_castle.png"))));
            this.deck.add(new EstateCard("Peril Palace", ImageIO.read(new File("src/resources/estate_peril_palace.png"))));
            this.deck.add(new WeaponCard("Broom", ImageIO.read(new File("src/resources/weapon_broom.png"))));
            this.deck.add(new WeaponCard("Scissors", ImageIO.read(new File("src/resources/weapon_scissors.png"))));
            this.deck.add(new WeaponCard("Knife", ImageIO.read(new File("src/resources/weapon_knife.png"))));
            this.deck.add(new WeaponCard("Shovel", ImageIO.read(new File("src/resources/weapon_shovel.png"))));
            this.deck.add(new WeaponCard("iPad", ImageIO.read(new File("src/resources/weapon_ipad.png"))));
        } catch (IOException e) {
            System.out.println("Image not found");
        }

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