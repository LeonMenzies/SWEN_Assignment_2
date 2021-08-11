package Gui;

import Cards.Card;
import Cards.EstateCard;
import Cells.Cell;
import Main.Game;
import Objects.Board;
import Objects.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MurderM extends Subject implements WindowListener, ComponentListener{

    private Game game = new Game(this);
    private final JFrame frame;
    private String[] names;
    private final Board board;
    private final BoardCanvas boardCanvas;
    JPanel handDisplay;
    JPanel guessDisplay;
    JPanel refuteDisplay;
    JLabel stepDisplay;
    JMenuItem i1, i2, i3;
    private JLabel currentPlayer;



    public MurderM(Board b, BoardCanvas bc) {
        this.board = b;
        this.boardCanvas = bc;
        super.addObserver(boardCanvas);
        this.frame = new JFrame();
    }

    public static void main(String[] args) {
        setUpGame();
    }


    /**
     * Method sets up the game by creating a new board and MurderM
     *
     */

    private static void setUpGame() {

        Board board = new Board(24, 24);
        board.setup();
        BoardCanvas boardCanvas = new BoardCanvas(board, board.getCells(), board.getCellImages(), board.getWeapons(), board.getEstates(), board.getPlayers());
        MurderM m = new MurderM(board, boardCanvas);
        m.guiSetup();
    }



    /**
     * Gets the board
     *
     * @return the current board
     */

    public Board getBoard() {
        return board;
    }


    /**
     * Method gets the game started by getting the playernames then creating a new game with this information then starting it
     */

    public void setUp() {
        setUpPlayerNames();
        if (names != null) {
            //sets the start button to disabled
            i1.setEnabled(false);
            this.game = new Game(this);
            game.setGameStarted(true);

            game.setUp(names);
            notifyObservers();
            game.playGame();
        }
    }


    /**
     * Sets up gui by adding all the buttons and menus to the frame then creating the color theme
     */

    public void guiSetup() {
        frame.setSize(800, 700);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        frame.setJMenuBar(addMenu());
        frame.getContentPane().add(boardCanvas, BorderLayout.CENTER);
        frame.getContentPane().add(createButtons(), BorderLayout.SOUTH);
        boardCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                int xClick = (int) Math.floor(e.getX() / (boardCanvas.getBounds().getWidth()/24.0));
                int yClick = (int) Math.floor(e.getY() / (boardCanvas.getBounds().getHeight()/24.0));
                if (xClick >= 0 && xClick <= 23 && yClick >= 0 && yClick <= 23) {

                    Cell selected = board.getCell(yClick, xClick);

                    if (game.getCurrent() != null && game.getGameStatus()) {
                        if (game.getCurrent().getSteps() != 0) {
                            if (game.getCurrent().move(board, selected)) {
                                displaySteps();
                                notifyObservers();
                            }
                        }
                    }
                }
            }
        });


        frame.getContentPane().add(createDisplay(), BorderLayout.EAST);
        frame.addWindowListener(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
            UIManager.put("control", new Color(58, 58, 59));
            UIManager.put("info", new Color(58, 58, 59));
            UIManager.put("nimbusBase", new Color(28, 28, 30));
            UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
            UIManager.put("nimbusDisabledText", new Color(58, 58, 59));
            UIManager.put("nimbusFocus", new Color(115, 164, 209));
            UIManager.put("nimbusGreen", new Color(176, 179, 50));
            UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
            UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
            UIManager.put("nimbusOrange", new Color(191, 98, 4));
            UIManager.put("nimbusRed", new Color(169, 46, 34));
            UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
            UIManager.put("nimbusSelectionBackground", new Color(72, 72, 74));
            UIManager.put("text", new Color(230, 230, 230));
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (UnsupportedLookAndFeelException exc) {
            System.err.println("Nimbus: Unsupported Look and feel!");
        }
        frame.setResizable(true);
        frame.addComponentListener(this);
        frame.setVisible(true);
    }

    /**
     *For each item that needs to be displayed makes sure they are all the same size and text is to the left and the Jlabels are center
     */

    public JPanel createDisplay() {

        this.currentPlayer = new JLabel("", JLabel.CENTER);
        this.currentPlayer.setPreferredSize(new Dimension(210, 5));
        this.currentPlayer.setHorizontalAlignment(JLabel.LEFT);
        this.currentPlayer.setVerticalAlignment(JLabel.CENTER);
        this.currentPlayer.setBorder(BorderFactory.createLineBorder(Color.white));

        this.stepDisplay = new JLabel("", SwingConstants.LEFT);
        this.stepDisplay.setPreferredSize(new Dimension(210, 5));
        this.stepDisplay.setHorizontalAlignment(JLabel.LEFT);
        this.stepDisplay.setVerticalAlignment(JLabel.CENTER);
        this.stepDisplay.setBorder(BorderFactory.createLineBorder(Color.white));

        this.handDisplay = new JPanel();
        this.handDisplay.setPreferredSize(new Dimension(210, 5));
        this.handDisplay.setBorder(BorderFactory.createLineBorder(Color.white));

        this.guessDisplay = new JPanel();
        this.guessDisplay.setPreferredSize(new Dimension(210, 5));
        this.guessDisplay.setBorder(BorderFactory.createLineBorder(Color.white));

        this.refuteDisplay = new JPanel();
        this.refuteDisplay.setPreferredSize(new Dimension(210, 5));
        this.refuteDisplay.setBorder(BorderFactory.createLineBorder(Color.white));

        JPanel playerInfo = new JPanel(new GridLayout(5, 1));
        playerInfo.add(currentPlayer);
        playerInfo.add(stepDisplay);
        playerInfo.add(handDisplay);
        playerInfo.add(guessDisplay);
        playerInfo.add(refuteDisplay);

        return playerInfo;
    }
    /**
     * Used to clear the panels to left of the board that hold the the players information
     */

    public void resetDisplay(){
        this.currentPlayer.setText("");

        this.stepDisplay.setText("");

        this.handDisplay.removeAll();
        this.handDisplay.revalidate();
        this.handDisplay.repaint();

        this.guessDisplay.removeAll();
        this.guessDisplay.revalidate();
        this.guessDisplay.repaint();

        this.refuteDisplay.removeAll();
        this.refuteDisplay.revalidate();
        this.refuteDisplay.repaint();
    }

    /**
     * creates a Jmenu with start,restart and quit and adds action listeners to them
     *
     * @return a new Jmenu
     */

    public JMenuBar addMenu() {

        JMenuBar menu = new JMenuBar();
        JMenu options = new JMenu("Options");

        i1 = new JMenuItem("Start");
        i2 = new JMenuItem("Restart");
        i2.setToolTipText("Restart the game");
        i3 = new JMenuItem("Quit");


        options.add(i1);
        i1.addActionListener(e -> this.setUp());
        i2.addActionListener(e -> this.restart());
        i3.addActionListener(e -> quit());
        options.add(i2);
        options.add(i3);
        menu.add(options);
        return menu;
    }

    /**
     * Method for setting the panel to the left of the board that states who's turn it is
     *
     * @param actualName players actual name they entered at the start
     * @param characterName the characters name
     */

    public void setCurrentPlayer(String actualName, String characterName) {
        //sets the icon to the image of said player to also help with clarification
        ImageIcon icon = new ImageIcon(game.getCurrent().getCellImage());
        currentPlayer.setIcon(icon);
        currentPlayer.setText("<html> " + "It's currently " + actualName + "'s (" + characterName + ")" + " turn" + "</html>");
    }


    /**
     * Updates the info panel to the left of the board with the players current step status
     */
    public void displaySteps() {
        //trys to set the label icon to the one in the resources
        Image image = null;
        try { image = ImageIO.read(new File("src/resources/dice.png"));
        } catch (IOException e) { e.printStackTrace(); }
        ImageIcon icon;
        if(image != null) {
            icon = new ImageIcon(image.getScaledInstance(48, 48, Image.SCALE_DEFAULT));
            stepDisplay.setIcon(icon);
        }


        //different messages are displayed depending where the player is in there turn
        if (!game.getCurrent().getRollStatus()) {
            stepDisplay.setText("<html> Please roll to get your steps </html>");
        } else if (game.getCurrent().getSteps() == 0) {
            stepDisplay.setText("<html> You are out of steps! </html>");
        } else {
            String sb = "<html> You rolled a " + game.getCurrent().getD1() + " and a " + game.getCurrent().getD2() + "<br/>" +
                    " You currently have " + game.getCurrent().getSteps() + " steps left" + "</html>";
            stepDisplay.setText(sb);
        }
    }

    /**
     * Method to set the visibility of the display hand panel
     *
     * @param b boolean true of false
     */
    public void setHandDisplay(Boolean b) {
        handDisplay.setVisible(b);
    }


    /**
     * Displays the current players hand on the player hand info panel
     *
     * @param hand players hand of cards
     */
    public void displayHand(List<Card> hand) {
        JLabel title = new JLabel("<html> Your current hand is: <br/></html>");
        this.handDisplay.add(title);

        for(int i = 0; i < hand.size(); i++){
            JLabel handLabel = new JLabel();
            ImageIcon icon = new ImageIcon(hand.get(i).getCardImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
            handLabel.setIcon(icon);
            handLabel.setText("<html> " + (i+1) + ". " + hand.get(i).getName() + "<br/></html>");
            this.handDisplay.add(handLabel);
        }
    }



    /**
     * Displays the current players guess for them and everyone refuting to see
     *
     * @param guess list of cards of the current players guess
     */
    public void displayGuess(List<Card> guess) {
        JLabel title = new JLabel("<html> The current guess by " + game.getCurrent().getActualName() + " is: <br/></html>");
        this.guessDisplay.add(title);

        for(int i = 0; i < guess.size(); i++){
            JLabel guessLabel = new JLabel();
            ImageIcon icon = new ImageIcon(guess.get(i).getCardImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
            guessLabel.setIcon(icon);
            guessLabel.setText("<html> " + (i+1) + ". " + guess.get(i).getName() + "<br/></html>");
            this.guessDisplay.add(guessLabel);
        }
    }

    /**
     * Displays the refutes made by other players to the current player
     *
     * @param refute list of cards made up of other players refutes
     */
    public void displayRefute(List<Card> refute) {
        JLabel title = new JLabel("<html> The refute for your guess is: <br/></html>");
        this.refuteDisplay.add(title);

        for(int i = 0; i < refute.size(); i++){
            JLabel refuteLabel = new JLabel();
            ImageIcon icon = new ImageIcon(refute.get(i).getCardImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
            refuteLabel.setIcon(icon);
            refuteLabel.setText("<html> " + (i+1) + ". " + refute.get(i).getName() + "<br/></html>");
            this.refuteDisplay.add(refuteLabel);
        }
    }



    /**
     * Creates the buttons for the panel adds in action listeners for the buttons as well a keyListener for the frame
     * Also adds pop up tool tips for the buttons
     *
     * @return a new Jpanel comprising of the buttons
     */
    public JPanel createButtons() {
        JPanel controls = new JPanel();

        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        //panel to hold the buttons down the bottom
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());

        JButton roll = new JButton("Roll (R)");
        roll.setFocusable(false);
        JButton guess = new JButton("Make Guess");
        guess.setFocusable(false);
        JButton finalGuess = new JButton("Make Final Guess");
        finalGuess.setFocusable(false);
        JButton endTurn = new JButton("End Turn (E)");
        endTurn.setFocusable(false);

        buttons.add(roll);
        buttons.add(guess);
        buttons.add(finalGuess);
        buttons.add(endTurn);

        //message that pops up when the buttons are hovered over
        roll.setToolTipText("Roll the dice to get your steps for this turn!");
        guess.setToolTipText("Make a guess and other players will have a chance to refute!");
        finalGuess.setToolTipText("Make a final accusation correct you win, fail you are out!");
        endTurn.setToolTipText("End your turn :(");


        roll.addActionListener(e -> game.roll());
        guess.addActionListener(e -> game.makeGuess());
        endTurn.addActionListener(e -> game.endTurn());
        finalGuess.addActionListener(e -> game.finalGuess());
        controls.add(buttons);

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == 69){
                    game.endTurn();
                } else if(e.getKeyCode() == 82){
                    game.roll();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        frame.setFocusable(true);

        return controls;
    }


    /**
     * Method for making a custom okay or cancel dialog panel
     *
     * @param message message to be displayed on dialog
     * @param title title of the dialog
     *
     * @return int of the option made by the user
     */
    public int displayOkOption(String message, String title) {
        return JOptionPane.showConfirmDialog(frame,
                message, title,
                JOptionPane.OK_CANCEL_OPTION);
    }

    /**
     * Displays a customer message to the frame via a dialog
     *
     * @param message message to be displayed
     */
    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    /**
     *Pop up dialog that displays who's turn it is at the start of their turn to make sure they have the tablet before continuing
     *
     * @param playerName name of player
     */
    public void displayPlayer(String playerName) {
        JOptionPane.showMessageDialog(frame, "It is " + playerName + "s turn please past them the tablet to them");
    }


    /**
     * Creates a new guess panel containing radio buttons of the list of cards the player can select from.
     *
     * @param tempDeck list of cards the player can make guess from
     *
     * @return a list of cards that are the players guess
     */

    public ArrayList<Card> makeGuess(ArrayList<Card> tempDeck) {
        ArrayList<Card> toReturn = new ArrayList<>();

        ArrayList<Card> buttons = new ArrayList<>();

        //goes through the tempdeck only adding the estate that the player is
        for (Card c : tempDeck) {
            if (c instanceof EstateCard) {
                if (!c.getName().equals(game.getCurrent().getEstateInString())) {
                    continue;
                }
            }
            buttons.add(c);

        }
        //new guess panel made from the list of cards
        GuessPanel jpane = new GuessPanel(buttons);

        //runs untill three cards are selected and they are returned or player can cancel and doesnt count as a guess
        while (true) {
            int result = JOptionPane.showConfirmDialog(null, jpane,
                    "Please Select three cards", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {

                //makes sure three cards are selected
                if (!jpane.cardCheck()) {
                    JOptionPane.showMessageDialog(frame,
                            "Select Three Cards",
                            "Card Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    toReturn = jpane.selectedCards();
                    break;
                }
            } else if (result == JOptionPane.CANCEL_OPTION) {
                break;
            }
        }


        return toReturn;
    }

    /**
     * Generates a refute panel for the selected player to make a refute with the cards in there hand
     *
     * @param guess list of the current guess
     * @param p the player makeing the refute
     * @return a card if they have a correct refute or null if they cant
     */

    public Card refute(ArrayList<Card> guess, Player p) {
        while (true) {
            RefutePanel rP = new RefutePanel(p.getHand());
            int result = JOptionPane.showConfirmDialog(null, rP,
                    "Its " + p.getName() + " turn to refute", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                Card c = rP.cardSelected();
                if (c == null) {
                    boolean canRefute = game.checkRefute(guess, p.getHand());
                    if (canRefute) {
                        JOptionPane.showMessageDialog(frame, "You can refute");
                    } else {
                        return null;
                    }
                } else {
                    boolean isRefute = game.isARefute(guess, c);
                    if (isRefute) {
                        return c;

                    } else {
                        JOptionPane.showMessageDialog(frame, "That's not a refute");
                    }
                }

            } else if (result == JOptionPane.CANCEL_OPTION) {
                this.errorMessage("Please enter a refute", "Select Refute");
            }
        }

    }

    /**
     * Method for generating an error message pop up dialog
     *
     * @param message message to be displayed on the dialog
     * @param title title of dialog
     */


    public void errorMessage(String message, String title) {
        JOptionPane.showMessageDialog(frame,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }


    /**
     * Pop up that gets all the player names for the game
     *
     *
     */
    public void setUpPlayerNames() {
        //game can be played with 3 or 4 players so that is first question asked
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
        //sets the length of the name array to what user specified
        if (n == JOptionPane.YES_OPTION) {
            names = new String[3];
        } else if (n == JOptionPane.NO_OPTION) {
            names = new String[4];
        }

        //checks that an option has been selected then creates a new panel
        if (names != null) {
            JPanel dialogPanel = new JPanel(new GridBagLayout());

            //builds a border around the panel
            Border titleBorder = BorderFactory.createTitledBorder("Player Information");
            Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
            Border combinedBorder = BorderFactory.createCompoundBorder(titleBorder, emptyBorder);
            dialogPanel.setBorder(combinedBorder);

            //creates a new TextFieldChecker
            JTextFieldChecker JC = new JTextFieldChecker();


           //loops through the number of players creates a new TextField adds them to the Textfield checker
           //then adds a label and the created grid to the panel
            for(int i = 0; i < names.length; i++){
                JTextField name = new JTextField(5);
                JC.addTextField(name);
                dialogPanel.add(new JLabel("Player " + (i+1)+  " Name:"), createGbc(0, i));
                dialogPanel.add(name, createGbc(1, i));
            }

            //stays true until all the players names have been entered
            while (true) {
                int result = JOptionPane.showConfirmDialog(null, dialogPanel,
                        "Please Enter Player Information", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {

                    //checks all textfields have been entered
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
            //adds the names of the players to the names array
            for (int i = 0; i < names.length; i++) {
                names[i] = JC.getTextFields().get(i).getText();
            }

        }
    }


    /**
     * Restarts the program by disposing the frame then calling setup again
     *
     */
    public void restart() {
        frame.dispose();
        this.game.setGameStarted(false);
        setUpGame();
    }


    /**
     * Method for quitting out of the program checks if user wants to then either exits or does nothing
     *
     */
    public void quit() {
        int result = this.displayOkOption("Do you want to Exit?", "Exit Confirmation");
        if (result == 0) {
            System.exit(0);
        } else if (result == 2) {
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }

    /**
     * On open window displays welcome message to users
     *
     */
    @Override
    public void windowOpened(WindowEvent e) {
        this.displayMessage("Welcome to Murder Mystery!");
    }



    /**
     * Method for quitting out of the program checks if user wants to then either exits or does nothing
     *
     * @param x  x location
     * @param y y location
     *
     * @return  a new GridBagConstraints with the specified x and y
     */
    private static GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.anchor =  GridBagConstraints.WEST;
        gbc.fill =  GridBagConstraints.BOTH;
        gbc.insets =  new Insets(5, 0, 5, 5);
        gbc.weightx =  0.1;
        gbc.weighty = 1.0;
        return gbc;
    }



    //unused implemented methods

    @Override
    public void windowClosing(WindowEvent e) {
        quit();
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


    @Override
    public void componentResized(ComponentEvent e) {
        boardCanvas.updateSize(boardCanvas.getBounds().width,boardCanvas.getBounds().height);
        boardCanvas.updateEstates(boardCanvas.getBounds().width,boardCanvas.getBounds().height);

    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}

