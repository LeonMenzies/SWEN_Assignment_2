package Gui;

import Cards.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 *  GuessPanel is a pop up panel comprising of JRadiobuttons with names to list of cards that can be selected by the user
 */
public class GuessPanel extends JPanel implements ActionListener {
    List<JRadioButton> buttons = new ArrayList<>();
    List<Card> buttonNames;


    public GuessPanel(ArrayList<Card> buttonNames) {
        super(new BorderLayout());
        this.buttonNames = buttonNames;

        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        //goes through the list of cards and creates buttons with there names then adds them to the panel
        for (Card c : buttonNames) {
            JRadioButton button = new JRadioButton(c.getName());
            button.setActionCommand("select");
            button.addActionListener(this);
            buttons.add(button);
            radioPanel.add(button);
        }

        add(radioPanel, BorderLayout.LINE_START);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }


    /**
     *  Checks to see if a count variable matchs how many cards should have been selected by the user
     *
     * @return true or false
     */
    public boolean cardCheck() {
        int count = 0;
        for (JRadioButton button : buttons) {
            if (button.isSelected()) {
                count++;
            }
        }
        return count == 3;
    }

    /**
     * Gets the cards selected by the user depending on the buttons they selected
     *
     * @return List of cards
     */
    public ArrayList<Card> selectedCards(){
        ArrayList<Card> toReturn = new ArrayList<>();
        for(int i = 0 ; i < buttonNames.size(); i++ ){
            if(buttons.get(i).isSelected()){
                toReturn.add(buttonNames.get(i));
            }
        }
        return toReturn;
    }

    /**
     *  After clicking checks to see if the button is one that can be selected or not. If it cant be selected set it to false
     *
     * @param e action event of clicking
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        //checks to see if was a select and not a deselect
        if (e.getActionCommand().equals("select")) {

            int playCount = 0;
            int weaponCount = 0;
            //goes through the first 4 cards cus they will be the player cards and sees if any of them are selected
            for (int i = 0; i < 4; i++) {
                if (buttons.get(i).isSelected()) {
                    playCount++;
                }
            }
            //similar for loop above but checks all weapon cards
            for (int i = 5; i < buttons.size(); i++) {
                if (buttons.get(i).isSelected()) {
                    weaponCount++;
                }
            }
            //if either count hits 2 means the button should not have been able to be selected and sets it to false
            if (playCount == 2 || weaponCount == 2) {
                for (JRadioButton button : buttons) {
                    if (button.equals(e.getSource())) {
                        button.setSelected(false);
                    }
                }
            }
        }
    }



}