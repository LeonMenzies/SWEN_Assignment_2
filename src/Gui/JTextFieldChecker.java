package Gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;

public class JTextFieldChecker implements DocumentListener {

    private List<JTextField> textFields = new ArrayList<JTextField>();

    public JTextFieldChecker() {

    }

    public void addTextField(JTextField textField) {
        textFields.add(textField);
        textField.getDocument().addDocumentListener(this);
    }

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

    private void checkData() {

    }
}
