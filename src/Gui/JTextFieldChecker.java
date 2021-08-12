package Gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.List;


/**
 * JTextFieldChecker is used to make sure that a list of textfields at least have one character in them
 */

public class JTextFieldChecker implements DocumentListener {

    private final List<JTextField> textFields = new ArrayList<>();

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
