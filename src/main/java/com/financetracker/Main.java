package com.financetracker;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize SQLite Database
        DatabaseHelper.initializeDatabase();

        // 2. Open Swing GUI safely on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}
