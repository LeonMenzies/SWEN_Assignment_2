import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Gui {

    private JFrame frame;


    public Gui(){
        setupGui();
    }

    public void setupGui() {
        frame = new JFrame("Murder Madness");
        frame.setSize(1000, 1000);
        frame.setMinimumSize(new Dimension(700, 700));
        frame.setMaximumSize(new Dimension(1000, 1000));
        frame.setLayout(new BorderLayout());

        //The display of the game state
        JPanel display = new JPanel();
        display.setLayout(new GridLayout(24, 24));
        frame.add(display, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        frame.add(buttons, BorderLayout.PAGE_END);

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

        addMenu();


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void addMenu(){
        JMenuItem i1, i2, i3;

        JMenuBar menu = new JMenuBar();
        JMenu options = new JMenu("Options");

        i1=new JMenuItem("Start");
        i2=new JMenuItem("Restart");
        i3=new JMenuItem("Quit");


        options.add(i1); options.add(i2); options.add(i3);
        menu.add(options);
        frame.setJMenuBar(menu);
    }

    public int closeDialog(){
        System.out.println("Closing?");
        return JFrame.EXIT_ON_CLOSE;
    }
}
