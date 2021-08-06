package Main;

import Cells.*;
import Cards.*;
import Gui.*;
import Objects.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game extends Subject implements WindowListener {
    ArrayList<Player> players = new ArrayList<>();
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
    JButton guess;
    JButton endTurn;
    JButton roll;
    JTextArea textDisplay;
    JTextArea textArea;
    Player winner = null;
    Player currentPlayer = null;
    //Pattern MovePat = Pattern.compile("[RGHFWASDE]");
    //Pattern dirPat = Pattern.compile("[WASD]");

    private String[] names;
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);

    private Thread gameLoop;
    private boolean isRunning;
    private static Board board;
    private static BoardCanvas boardCanvas;
    private JFrame frame;
    private static Game game;


    public Game() {
        super.addObserver(boardCanvas);
    }

    public static void main(String[] args) {


        board = new Board(24, 24);
        board.setup();
        boardCanvas = new BoardCanvas(board, board.getCells(), board.getCellImages(), board.getWeapons(), board.getEstates(), board.getPlayers());
        game = new Game();

        game.setupGui();

    }


    /**
     * Allows the player to play game via a serious of inputs into the console
     *
     * @param p current player
     * @return true of false if the player has won
     */

    public boolean playersTurn(Player p) {

        Scanner input = new Scanner(System.in);
        String in;
        while (true) {


            //sets up the player for there turn clears all the variables from last turn
            if (!p.getTurn()) {
                System.out.print("It is " + p.getName() + "'s turn please make sure they have the tablet and enter any key to continue: ");
                input.next();
                p.setTurn(true);
                p.setRollStatus(false);
                p.setGuessStatus(true);
                p.clearVisited();

            }


            guess.addActionListener(e -> makeGuess());
            //Display the board before the players turn and there steps
            System.out.println(board);
            System.out.println(p.getName() + " has " + p.getSteps() + " number of steps");

            //if player has not rolled option is displayed
            if (!p.getRollStatus()) {
                System.out.println("R(Roll), H(Show Hand), G(guess), F(Final Guess), E(END TURN) or WASD(Objects.Move)");
            } else {
                System.out.println("H(Show Hand), G(guess), F(Final Guess), E(END TURN) or WASD(Objects.Move)");
            }

            in = input.next().toUpperCase();

            //      in = checkInput(in);

            //rolls the dice for the player and makes so they cant roll again
            if (in.equals("R") && !p.getRollStatus()) {
                p.roll();
                p.setRollStatus(true);

            } else if (in.equals("R") && p.getRollStatus()) {
                System.out.println("Already Rolled");


            }
            //if input matches a move direction moves the player and updates there steps unless they are out of steps
//            Matcher matcher = dirPat.matcher(in);
//            boolean matchFound = matcher.matches();
//            if (matchFound && p.getSteps() != 0) {
//                p.move(board, in);
//                this.notifyObservers();
//
//            } else if (matchFound && p.getSteps() == 0) {
//                System.out.println("You are out of steps or please roll");
//
//            }

            if (in.equals("H")) {
                p.printHand();

            }

            //set so player cant make another guess this turn, gets the order the following players will make a guess
            //adds the cards to the refute list, clears the screen then displays the refute cards for the player
            //also checks to see if the play is in an estate
            if (in.equals("G") && !p.getGuessStatus() && !p.getEstateInString().equals("null")) {
                p.setGuessStatus(true);
                refuteOrder(p);
                makeGuess(p);
                refuteCards.clear();

                //      refute(p.getGuess());

                System.out.println("Please past the tablet back to " + p.getName());
                input.next();
                System.out.println("Refute cards are: ");
                for (int i = 0; i < refuteCards.size(); i++) {
                    System.out.println(i + ": " + refuteCards.get(i).getName());
                }

            } else if (in.equals("G") && p.getGuessStatus() && !p.getEstateInString().equals("null")) {
                System.out.println("You have already guessed");

            } else if (in.equals("G") && !p.getGuessStatus() && p.getEstateInString().equals("null")) {
                System.out.println("You need to be in an estate to make a guess");
            }

            //player makes a guess then the cards a checked against the circumstance
            //if player is successful they have won otherwise they are out
            //also checks to see if the player is in a estate
            if (in.equals("F") && !p.getGuessStatus() && !p.getEstateInString().equals("null")) {
                makeGuess(p);
                p.setHasWon(checkWin(p.getGuess()));
                if (p.getHasWon()) {
                    winner = p;
                    return true;
                } else {
                    p.setIsout(true);
                    System.out.println("You are out " + p.getName() + " you can't guess or move but can still refute");
                }
                return false;
            } else if (in.equals("F") && !p.getGuessStatus() && p.getEstateInString().equals("null")) {
                System.out.println("You must be in an estate to make a final guess");
            }

            //ends the players turn
            if (in.equals("E") && (p.getSteps() == 0 || !p.getEstateInString().equals("null"))) {
                p.clearSteps();
                p.setTurn(false);

                return false;
            } else if (in.equals("E") && (p.getSteps() != 0 || p.getEstateInString().equals("null"))) {
                System.out.println("You must be in an estate or out of steps to end your turn");
            }
        }

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


    public void setUpGameLoop() {
        gameLoop = new Thread(() -> {
            while (isRunning) {
                while (!gameWon) {
                    for (Player p : players) {
                        if (!p.getIsOut()) {
                            JOptionPane.showMessageDialog(frame, "It is " + p.getName() + " turn please past the tablet to them");

                            currentPlayer = p;

                            p.setTurn(true);
                            notifyObservers();

                            playersturn(p);
                        }
                    }
                }

            }
        });
    }

    public void playersturn(Player p) {
        p.setRollStatus(false);
        p.setGuessStatus(false);
        p.clearVisited();

        while (p.getTurn()) {

            roll.addActionListener(e -> {
                if(!p.getRollStatus()){
                    p.roll();
                    p.setRollStatus(true);
                    System.out.println(p.getSteps());
                }
            });

            guess.addActionListener(e -> makeGuess());
            endTurn.addActionListener(e -> endTurn());


            if(!p.getTurn()){
                break;
            }
        }
    }

    public void endTurn(){

        if(currentPlayer.getTurn()) {

            JPanel jpane = new JPanel();
            int result =  JOptionPane.showConfirmDialog(jpane,
                    "Do you want to end your turn?", "End Turn: ",
                    JOptionPane.OK_CANCEL_OPTION);

            if(result == JOptionPane.OK_OPTION){
                if(currentPlayer.getSteps() == 0 || !currentPlayer.getEstateInString().equals("null")){
                    currentPlayer.clearSteps();
                    currentPlayer.setTurn(false);
                }else{
                    JOptionPane.showMessageDialog(frame,
                            "You must be out of steps to end or in an Estate to end your turn",
                            "End Turn Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }

    public void makeGuess() {
        System.out.println("guess");
        if (!currentPlayer.getGuessStatus()) {

            ArrayList<Card> buttons = new ArrayList<>();
            guessDeck.clear();
            refuteCards.clear();
            for (Card c : tempDeck) {
                if (c instanceof EstateCard) {
                    if (!c.getName().equals("Haunted House")) {
                        continue;
                    }
                }
                buttons.add(c);

                guessDeck.add(c.clone());


            }
            GuessPanel jpane = new GuessPanel(buttons);


            while (true) {
                int result = JOptionPane.showConfirmDialog(null, jpane,
                        "Please Select three cards", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {

                    if (!jpane.cardCheck()) {
                        JOptionPane.showMessageDialog(frame,
                                "Select Three Cards",
                                "Card Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        guessDeck = jpane.selectedCards();
                        break;
                    }
                } else if (result == JOptionPane.CANCEL_OPTION) {
                    break;
                }
            }
            currentPlayer.setGuessStatus(true);
            refute(guessDeck);
        }

    }

    public void refute(List<Card> guess) {
        refuteOrder(currentPlayer);
        //display guess

        for (Player p : tempPlayers) {
            while (true) {
                RefutePanel rP = new RefutePanel(p.getHand());
                int result = JOptionPane.showConfirmDialog(null, rP,
                        "Its " + p.getName() + " turn to refute", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    Card c = rP.cardSelected();
                    if (c == null) {
                        boolean canRefute = checkRefute(guess, p.getHand());
                        if (canRefute) {
                            JOptionPane.showMessageDialog(frame, "You can refute");
                        } else {
                            break;
                        }
                    } else {
                        boolean isRefute = isARefute(guess, c);
                        if (isRefute) {
                            refuteCards.add(c);
                            break;
                        } else {
                            JOptionPane.showMessageDialog(frame, "That's not a refute");
                        }
                    }

                } else if (result == JOptionPane.CANCEL_OPTION) {
                    JOptionPane.showMessageDialog(frame,
                            "Please enter a refute",
                            "Select Refute",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }

//    /**
//     * Goes through all the players aside from the guesser and checks if they can refute
//     *
//     * @param guess list of the current guess
//     */
//    public void refute(List<Card> guess) {
//        //goes through the list of players in the correct refute order displaying the current guess cards
//        for (int i = 0; i < tempPlayers.size(); i++) {
//            while (true) {
//                System.out.println("The current Guess is:");
//                for (Card c : guess) {
//                    System.out.print(c.getName() + " ");
//                }
//                System.out.println();
//                //prints out who's turn it is to refute and there cards
//                Scanner input = new Scanner(System.in);
//                System.out.println("It is " + tempPlayers.get(i).getName() + "'s time to make a refute ");
//                tempPlayers.get(i).printHand();
//                System.out.println("Please pick a card that refutes eg 1 or enter 4 if you cant refute");
//                String in = input.next();
//                //method checks to see if they have entered a number
//                if (isNumeric(in)) {
//                    int j = Integer.parseInt(in.substring(0, 1));
//                    if (j > tempPlayers.get(i).getHand().size() - 1 && j != 4) {
//                        System.out.println("Please enter a valid number");
//
//                    }
//                    //following ifs check if they can refute or if the refute is legit
//                    else if (j == 4) {
//                        boolean check = checkRefute(guess, tempPlayers.get(i).getHand());
//                        if (check) {
//                            System.out.println("You can refute please try again");
//
//                        } else {
//                            break;
//                        }
//                    } else {
//                        //pulls the card and checks it is a refute
//                        Card r = tempPlayers.get(i).getHand().get(j);
//                        boolean isRefute = isARefute(guess, r);
//                        //if refute is legit card is added to one to show player making the guess
//                        if (isRefute) {
//                            //player is removed from the list and the next player goes
//                            Player temp = players.get(i);
//                            tempPlayers.remove(temp);
//                            refuteCards.add(r);
//                            break;
//                        } else {
//                            System.out.println("This refute is incorrect try again or select can't refute");
//
//                        }
//                    }
//
//
//                }
//
//
//            }
//
//        }
//
//    }

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
     * Checks to see if a String is an int
     *
     * @param str String to be checked
     * @return true or false if can be parsed as int or not
     */
    public boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Allows the play to make a guess of one of each card, the estate being the estate they are currently in
     *
     * @param p player making the guess
     */

    public void makeGuess(Player p) {

        //clears the current players guess array and prints all the cards in the deck for them
        p.clearGuess();
        guessDeck.clear();
        int count = 0;
        for (Card c : tempDeck) {
            if (c instanceof EstateCard) {
                if (!c.getName().equals(p.getEstateInString())) {
                    continue;
                }
            }
            guessDeck.add(c.clone());
            System.out.println(count + ": " + c.getName());
            count++;
        }

        //int and strings required for selecting the right cards from the deck
        int i;
        int j;
        int k;
        String cardC;
        String cardE;
        String cardW;
        while (true) {
            Scanner input = new Scanner(System.in);
            String in;
            System.out.println("Please select 3 cards as your guess eg 1,4,9");
            in = input.next();

            if (in.length() != 5) {
                System.out.println("Please select three cards");

            } else {
                //brings the input into substrings of the correct numbers
                cardC = in.substring(0, 1);
                cardE = in.substring(2, 3);
                cardW = in.substring(4, 5);


                //need to check that the strings input are actually numbers
                if (isNumeric(cardC) && isNumeric(cardE) && isNumeric(cardW)) {
                    i = Integer.parseInt(cardC);
                    j = Integer.parseInt(cardE);
                    k = Integer.parseInt(cardW);
                    //cards are in order from character to estate to weapon so if one of the numbers is out means player grabbed two or more of one type
                    if (i > 3 || j != 4 || (k < 5 || k > 9)) {
                        System.out.println("Please pick one of each card");

                    } else {
                        break;
                    }
                }
            }


        }
        //generates the three cards of each type that the play is guessing
        //and add them to the players guess array
        CharacterCard gWho = (CharacterCard) guessDeck.get(i);
        EstateCard gWhere = (EstateCard) guessDeck.get(j);
        WeaponCard gWhat = (WeaponCard) guessDeck.get(k);
        p.addGuess(gWho);
        p.addGuess(gWhere);
        p.addGuess(gWhat);

        moveCharacters(p, gWhat, gWho);

    }

    /**
     * Objects.Move the the weapon and player the on the board into the estate that the player is making the guess in
     *
     * @param player player making the guess
     * @param gWhat  current weapon card guess
     * @param gWho   current player card guess
     */

    public void moveCharacters(Player player, WeaponCard gWhat, CharacterCard gWho) {


        Player pl;
        Estate we;
        Estate e = player.getEstateIn();

        //Goes through the list of weapons finds the one that match's that of the guess removes it from its current estate into the new one
        for (Weapon w1 : board.getWeapons()) {
            if (w1.getWepName().equals(gWhat.getName())) {

                we = w1.getEstate();
                if (!we.getEstateName().equals(e.getEstateName())) {
                    we.removeWeaponInEstate(w1);
                    e.addWeaponInEstate(w1);
                }
            }
        }


        //goes through the list of players finding the one that match's the guess if they are already in an estate removes from that estate and into the new one
        //if they are somewhere on the board goes through the board and replaces them with a free cell
        for (Player p1 : players) {
            if (p1.getName().equals(gWho.getName())) {
                pl = p1;

                if (pl.getEstateIn() != null) {

                    Estate es = pl.getEstateIn();
                    if (!es.getEstateName().equals(e.getEstateName())) {
                        es.removePlayersInEstate(pl);
                        e.addPlayersInEstate(pl);
                    }

                } else {
                    for (int i = 0; i < board.getCells().length; i++) {
                        for (int j = 0; j < board.getCells().length; j++) {
                            if (board.getCell(i, j) instanceof PlayerCell) {
                                PlayerCell pc = (PlayerCell) board.getCell(i, j);
                                if (pc.toString().equals(pl.toString())) {
                                    board.redrawCell(i, j, new FreeCell(i, j, board.getCellImages().get("__")));
                                }
                            }
                        }
                    }
                    e.addPlayersInEstate(pl);
                }

            }
        }

        boardCanvas.repaint();

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

        //4 player gets added in if necessary
        if (numPlayers == 4) {
            players.add(new Player("Percy", 14, 22, board.getCellImages().get("Percy")));
        }


        for (Player p : players) {
            board.addPlayer(p);
        }


        boardCanvas.repaint();

    }

    public void startgame() {
        setUpDeck();

        generateMurder();
        dealCards();

        Random rand = new Random();
        int i = rand.nextInt(players.size());
        Player p = players.get(i);
        //then generates the starting order based on who starts first
        generateStartingOrder(p);

        this.isRunning = true;
        this.gameLoop.start();
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

    /**
     * Clears the console screen so other players can't see what the other players have done
     */


    public void setupGui() {



        frame = new JFrame("Murder Madness");
        frame.setSize(576, 720);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        boardCanvas.setSize(576, 576);
        frame.add(boardCanvas, BorderLayout.CENTER);

        boardCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                int xClick = Math.round(e.getX() / 24);
                int yClick = Math.round(e.getY() / 24);


                if (xClick >= 0 && xClick <= 23 && yClick >= 0 && yClick <= 23) {

                    if(currentPlayer.move(board, xClick, yClick)){
                        notifyObservers();
                    } else {
                        UIPrint("Invalid Move");
                    }


                }
            }
        });



        this.setUpGameLoop();
        //Panel to hold the buttons and the text panel
        JPanel controls = new JPanel();

        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        //panel to hold the buttons down the bottom
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());

        roll = new JButton("Roll");
        JButton showHand = new JButton("Show Hand");
        guess = new JButton("Make Guess");
        JButton finalGuess = new JButton("Make Final Guess");
        endTurn = new JButton("End Turn");

        buttons.add(roll);
        buttons.add(showHand);
        buttons.add(guess);
        buttons.add(finalGuess);
        buttons.add(endTurn);

        //Text area for information
        textDisplay = new JTextArea();
        textDisplay.setEditable(false);
        DefaultCaret caret = (DefaultCaret)textDisplay.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);


        controls.add(textDisplay);
        controls.add(buttons);

        frame.add(controls, BorderLayout.PAGE_END);


        addMenu();

        //Create the text field
        frame.addWindowListener(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
            UIManager.put("control", new Color(58,58,59));
            UIManager.put("info", new Color(58,58,59));
            UIManager.put("nimbusBase", new Color(28, 28, 30));
            UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
            UIManager.put("nimbusDisabledText", new Color(58,58,59));
            UIManager.put("nimbusFocus", new Color(115, 164, 209));
            UIManager.put("nimbusGreen", new Color(176, 179, 50));
            UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
            UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
            UIManager.put("nimbusOrange", new Color(191, 98, 4));
            UIManager.put("nimbusRed", new Color(169, 46, 34));
            UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
            UIManager.put("nimbusSelectionBackground", new Color(72,72,74));
            UIManager.put("text", new Color(230, 230, 230));
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (UnsupportedLookAndFeelException exc) {
            System.err.println("Nimbus: Unsupported Look and feel!");
        }

        frame.setVisible(true);
    }

    public void UIPrint(String s){
        textDisplay.setText("Steps: " + currentPlayer.getSteps() + " | " + s + "\n");
    }

    public void addMenu() {
        JMenuItem i1, i2, i3;

        JMenuBar menu = new JMenuBar();
        JMenu options = new JMenu("Options");

        i1 = new JMenuItem("Start");
        i2 = new JMenuItem("Restart");
        i3 = new JMenuItem("Quit");

        i1.addActionListener(e -> startgame());

        options.add(i1);
        options.add(i2);
        options.add(i3);
        menu.add(options);
        frame.setJMenuBar(menu);
    }


    @Override
    public void windowOpened(WindowEvent e) {
        Object[] options = {"3",
                "4"};
        int n = JOptionPane.showOptionDialog(frame,
                "How many players?",
                "Players",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (n == JOptionPane.YES_OPTION) {
            names = new String[3];
        } else if (n == JOptionPane.NO_OPTION) {
            names = new String[4];
        }

        game.playerSetUp(names.length);
        game.weaponSetup();


        JPanel dialogPanel = new JPanel(new GridBagLayout());

        Border titleBorder = BorderFactory.createTitledBorder("Player Information");
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border combinedBorder = BorderFactory.createCompoundBorder(titleBorder, emptyBorder);
        dialogPanel.setBorder(combinedBorder);
        JTextField player1Name = new JTextField(5);
        JTextField player2Name = new JTextField(5);
        JTextField player3Name = new JTextField(5);

        player1Name.setText("Harry");
        player2Name.setText("Leon");
        player3Name.setText("Terry");


        JTextFieldChecker JC = new JTextFieldChecker();
        JC.addTextField(player1Name);
        JC.addTextField(player2Name);
        JC.addTextField(player3Name);



        dialogPanel.add(new JLabel("Player 1 Name:"), createGbc(0, 0));
        dialogPanel.add(player1Name, createGbc(1, 0));
        dialogPanel.add(new JLabel("Player 2 Name:"), createGbc(0, 1));
        dialogPanel.add(player2Name, createGbc(1, 1));
        dialogPanel.add(new JLabel("Player 3 Name:"), createGbc(0, 2));
        dialogPanel.add(player3Name, createGbc(1, 2));
        if (names.length == 4) {
            JTextField player4Name = new JTextField(5);
            JC.addTextField(player4Name);
            dialogPanel.add(new JLabel("Player 4 name:"), createGbc(0, 3));
            dialogPanel.add(player4Name, createGbc(1, 3));
        }
        while (true) {
            int result = JOptionPane.showConfirmDialog(null, dialogPanel,
                    "Please Enter Player Information", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {

                if (!JC.isDataEntered()) {
                    JOptionPane.showMessageDialog(frame,
                            "All player names must be entered",
                            "Name's Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    break;
                }
            }
        }
    }

    private static GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.anchor = (x == 0) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
        gbc.fill = (x == 0) ? GridBagConstraints.BOTH
                : GridBagConstraints.HORIZONTAL;

        gbc.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
        gbc.weightx = (x == 0) ? 0.1 : 1.0;
        gbc.weighty = 1.0;
        return gbc;
    }


    @Override
    public void windowClosing(WindowEvent e) {

        int result = JOptionPane.showConfirmDialog(frame,
                "Do you want to Exit ?", "Exit Confirmation : ",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else if (result == JOptionPane.NO_OPTION) {
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }


}