import java.awt.*;
import javax.swing.*;

public class Gui {

    private JFrame frame;


    public Gui(){
        setupGui();
    }

    public void setupGui() {

        frame = new JFrame();
        frame.setSize(1000, 1000);
        frame.setLayout(new BorderLayout());



        //The display of the game state
        JPanel display = new JPanel();
        frame.add(display, BorderLayout.CENTER);

        addMenu();

        //Buttons
        JButton roll = new JButton();
        JButton makeGuess = new JButton();
        frame.add(roll, BorderLayout.PAGE_END);
        frame.add(makeGuess, BorderLayout.PAGE_END);


        //Create the text field
        JTextField textInput = new JTextField();
        frame.add(textInput, BorderLayout.PAGE_END);

        frame.setDefaultCloseOperation(closeDialog());
        frame.setVisible(true);
    }

    public void addMenu(){
        JMenuItem i1, i2, i3;

        JMenuBar menu = new JMenuBar();
        JMenu options = new JMenu("Options");

        i1=new JMenuItem("Restart");
        i2=new JMenuItem("Preferences");
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
