package com.financetracker;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // check if new user

        // Initialize SQLite Database
        DatabaseHelper.initializeDatabase();

        // Open Swing GUI safely on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            if(DatabaseHelper.newUser()){
                SwingUtilities.invokeLater(() -> {
                    EnterFinancesGUI enterFinancesGUI = new EnterFinancesGUI();
                    enterFinancesGUI.setVisible(true);
                });
            }else{
                MainGUI mainGUI = new MainGUI();
                mainGUI.setVisible(true);
                }
        });
    }
}
