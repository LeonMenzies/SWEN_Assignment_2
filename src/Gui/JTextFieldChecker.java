package Gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;


/**
 * JTextFieldChecker is used to make sure that a list of textfields at least have one character in them
 */

public class JTextFieldChecker implements DocumentListener {

    private List<JTextField> textFields = new ArrayList<JTextField>();

    public JTextFieldChecker() {

    }


    /**
     * Adds a Jtextfield to list of Jtextfields
     *
     * @param textField  textfield to be added to the list
     */
    public void addTextField(JTextField textField) {
        textFields.add(textField);
        textField.getDocument().addDocumentListener(this);
    }

    /**
     * Method goes through each Jtextfield for the list and checks if the text length is == 0
     * Used to make sure all fields are entered
     *
     * @return false if at least one is 0 true otherwise
     */
    public boolean isDataEntered() {
        for (JTextField textField : textFields) {
            if (textField.getText().trim().length() == 0)
                return false;
        }

        return true;
    }


    /**
     * Gets the list of textfields
     *
     * @return this's list of textfields
     */
    public List<JTextField> getTextFields(){
        return textFields;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {

    }

    @Override
    public void removeUpdate(DocumentEvent e) {

    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }



}
