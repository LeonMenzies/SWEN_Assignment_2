package Gui;

import Cards.Card;
import Cards.EstateCard;
import Cells.Cell;
import Main.Game;
import Objects.Board;
import Objects.Player;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class MurderM extends Subject implements WindowListener {

    private Game game = new Game(this);
    private final JFrame frame;
    private String[] names;
    private final Board board;
    private final BoardCanvas boardCanvas;
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);

    public MurderM(Board b, BoardCanvas bc) {
        this.board = b;
        this.boardCanvas = bc;
        super.addObserver(boardCanvas);
        this.frame = new JFrame();
    }

    public Board getBoard() {
        return board;
    }

    public void setUp() {
        setUpPlayerNames();
        if(names != null) {
            this.game = new Game(this);
            game.setGameStarted(true);

            game.setUp(names.length);
            notifyObservers();
            game.playGame();
        }
    }

    public void guiSetup() {

        frame.setSize(576, 720);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        frame.setJMenuBar(addMenu());
        frame.getContentPane().add(createCanvas(), BorderLayout.CENTER);
        frame.getContentPane().add(createButtons(), BorderLayout.PAGE_END);
        boardCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                int xClick = (int) Math.floor(e.getX() / 24.0);
                int yClick = (int) Math.floor(e.getY() / 24.0);

                if (xClick >= 0 && xClick <= 23 && yClick >= 0 && yClick <= 23) {

                    Cell selected = board.getCell(yClick, xClick);

                    if (game.getCurrent() != null ) {
                        if(game.getCurrent().getSteps() != 0) {
                            if (game.getCurrent().move(board, selected)) {
                                notifyObservers();
                            }
                        }else{
                            errorMessage("You are out of steps or need to roll!", "Invalid Move");
                        }
                    }
                }
            }
        });
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

        frame.setVisible(true);
    }


    public JMenuBar addMenu() {

        JMenuItem i1, i2, i3;

        JMenuBar menu = new JMenuBar();
        JMenu options = new JMenu("Options");

        i1 = new JMenuItem("Start");
        i2 = new JMenuItem("Restart");
        i3 = new JMenuItem("Quit");


        options.add(i1);
        i1.addActionListener(e -> this.setUp());
        i2.addActionListener(e -> this.restart());
        i3.addActionListener(e->quit());
        options.add(i2);
        options.add(i3);
        menu.add(options);
        return menu;
    }

    public JPanel createCanvas() {

        return boardCanvas;
    }

    public JPanel createButtons() {
        JPanel controls = new JPanel();

        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        //panel to hold the buttons down the bottom
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());

        JButton roll = new JButton("Roll");
        JButton showHand = new JButton("Show Hand");
        JButton guess = new JButton("Make Guess");
        JButton finalGuess = new JButton("Make Final Guess");
        JButton endTurn = new JButton("End Turn");

        buttons.add(roll);
        buttons.add(showHand);
        buttons.add(guess);
        buttons.add(finalGuess);
        buttons.add(endTurn);
        roll.addActionListener(e -> game.roll());
        guess.addActionListener(e -> game.makeGuess());
        endTurn.addActionListener(e -> game.endTurn());
        finalGuess.addActionListener(e -> game.finalGuess());

        //Text area for information
        JTextArea textDisplay = new JTextArea();
        textDisplay.setEditable(false);
        DefaultCaret caret = (DefaultCaret) textDisplay.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);


        controls.add(textDisplay);
        controls.add(buttons);

        return controls;
    }


    public int displayOkOption(String message, String title) {

        return JOptionPane.showConfirmDialog(frame,
                message, title,
                JOptionPane.OK_CANCEL_OPTION);
    }

    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }


    public void displayPlayer(String playerName) {
        JOptionPane.showMessageDialog(frame, "It is " + playerName + " turn please past them the tablet to them");
    }


    public ArrayList<Card> makeGuess(ArrayList<Card> tempDeck) {
        ArrayList<Card> toReturn = new ArrayList<>();

        ArrayList<Card> buttons = new ArrayList<>();

        for (Card c : tempDeck) {
            if (c instanceof EstateCard) {
                if (!c.getName().equals(game.getCurrent().getEstateInString())) {
                    continue;
                }
            }
            buttons.add(c);

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
                    toReturn = jpane.selectedCards();
                    break;
                }
            } else if (result == JOptionPane.CANCEL_OPTION) {
                break;
            }
        }


        return toReturn;
    }

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


    public void errorMessage(String message, String title) {
        JOptionPane.showMessageDialog(frame,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    public void setUpPlayerNames(){
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

        if(names !=  null) {
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
    }

    public void restart(){
        frame.dispose();
        Board board = new Board(24, 24);
        board.setup();

        BoardCanvas boardCanvas = new BoardCanvas(board, board.getCells(), board.getCellImages(), board.getWeapons(), board.getEstates(), board.getPlayers());
        boardCanvas.setSize(576, 576);
        MurderM m = new MurderM(board, boardCanvas);
        m.guiSetup();
    }

    public void quit(){
        int result = this.displayOkOption("Do you want to Exit?", "Exit Confirmation");
        if (result == 0) {
           System.exit(0);
        } else if (result == 2) {
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }


    @Override
    public void windowOpened(WindowEvent e) {
      this.displayMessage("Welcome to Murder Mystery!");
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

    public static void main(String[] args) {
        Board board = new Board(24, 24);
        board.setup();

        BoardCanvas boardCanvas = new BoardCanvas(board, board.getCells(), board.getCellImages(), board.getWeapons(), board.getEstates(), board.getPlayers());
        boardCanvas.setSize(576, 576);
        MurderM m = new MurderM(board, boardCanvas);
        m.guiSetup();

    }
}

