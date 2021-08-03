package Gui;

import Cards.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class RefutePanel extends JPanel implements ActionListener {
    List<JRadioButton> buttons = new ArrayList<>();
    List<Card> buttonNames;


    public RefutePanel(List<Card> buttonNames) {
        super(new BorderLayout());
        this.buttonNames = buttonNames;

        for (Card c : buttonNames) {
            JRadioButton button = new JRadioButton(c.getName());
            button.setActionCommand("select");
            button.addActionListener(this);
            buttons.add(button);
        }
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

    public Card cardSelected(){
        Card toReturn = null;

        for(int i = 0; i < buttons.size(); i++){
            if(buttons.get(i).isSelected()){
                toReturn = buttonNames.get(i);
            }
        }
        return toReturn;
    }
}