package Gui;

import Cards.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/***
 * The RefutePanel is a gui component shown when a refute is to be made during gameplay
 */
public class RefutePanel extends JPanel implements ActionListener {
    List<JRadioButton> buttons = new ArrayList<>();
    List<Card> buttonNames;


    public RefutePanel(List<Card> buttonNames) {
        super(new BorderLayout());
        this.buttonNames = buttonNames;

        //goes through the list of cards and generates a button with that cards name
        for (Card c : buttonNames) {
            JRadioButton button = new JRadioButton(c.getName());
            button.setActionCommand("select");
            button.addActionListener(this);
            buttons.add(button);

        }
        //adds a cant refute button just incase player cant refute
        JRadioButton cant = new JRadioButton("Can't Refute");
        cant.setActionCommand("select");
        cant.addActionListener(this);
        buttons.add(cant);
        //creates a button group and panel and the buttons are added to both of them
        //button group means only one can be selected at a time
        ButtonGroup bG = new ButtonGroup();
        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        for (JRadioButton jB : buttons) {
            radioPanel.add(jB);
            bG.add(jB);
        }
        add(radioPanel, BorderLayout.LINE_START);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }


    /**
     * Goes through the buttons find which one is selected then sets the card to that from the list of cards
     *
     * @return card or null
     */
    public Card cardSelected(){
        Card toReturn = null;

        for(int i = 0; i < buttons.size()-1; i++){
            if(buttons.get(i).isSelected()){
                toReturn = buttonNames.get(i);
            }
        }
        return toReturn;
    }
}
