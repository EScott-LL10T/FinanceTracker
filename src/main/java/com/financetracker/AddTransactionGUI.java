package com.financetracker;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class AddTransactionGUI extends JFrame {
    private final MainGUI mainGUI;

    public AddTransactionGUI(MainGUI mainGUI){
        this.mainGUI = mainGUI;

        setTitle("Personal Finance Tracker");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers window on screen

        setLayout(new BorderLayout());



        Font buttonFont = new Font("Ariel", Font.PLAIN, 14);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(buttonFont);

        JButton addTransactionButton = new JButton("Add Transaction");
        addTransactionButton.setFont(buttonFont);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 80, 80));
        buttonPanel.add(cancelButton);
        buttonPanel.add(addTransactionButton);

        add(buttonPanel, BorderLayout.SOUTH);

    }
}
