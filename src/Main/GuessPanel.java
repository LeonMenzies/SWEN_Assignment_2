package Main;

import Cards.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GuessPanel extends JPanel implements ActionListener {
    List<JRadioButton> buttons = new ArrayList<>();
    List<Card> buttonNames;


    public GuessPanel(ArrayList<Card> buttonNames) {
        super(new BorderLayout());
        this.buttonNames = buttonNames;

        for (Card c : buttonNames) {
            JRadioButton button = new JRadioButton(c.getName());
            button.setActionCommand("select");
            button.addActionListener(this);
            buttons.add(button);
        }
        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        for (JRadioButton jB : buttons) {
            radioPanel.add(jB);
        }
        add(radioPanel, BorderLayout.LINE_START);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
    public boolean cardCheck() {
        int count = 0;
        for (JRadioButton button : buttons) {
            if (button.isSelected()) {
                count++;
            }
        }
        return count == 3 || count == 0;
    }

    public ArrayList<Card> selectedCards(){
        ArrayList<Card> toReturn = new ArrayList<>();
        for(int i = 0 ; i < buttonNames.size(); i++ ){
            if(buttons.get(i).isSelected()){
                toReturn.add(buttonNames.get(i));
            }
        }
        return toReturn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("select")) {
            int playCount = 0;
            int weaponCount = 0;
            for (int i = 0; i < 4; i++) {
                if (buttons.get(i).isSelected()) {
                    playCount++;
                }
            }

            for (int i = 5; i < buttons.size(); i++) {
                if (buttons.get(i).isSelected()) {
                    weaponCount++;
                }
            }
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