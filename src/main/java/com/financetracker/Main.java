package com.financetracker;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // check if new user

        // Initialize SQLite Database
        DatabaseHelper.initializeDatabase();

        // Open Swing GUI safely on the Event Dispatch Thread
        JFrame frame = new JFrame();

        frame.setTitle("Finance Tracker");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> {
            if(DatabaseHelper.newUser()){
                SwingUtilities.invokeLater(() -> {


                    CreateProfilePanel profilePanel = new CreateProfilePanel(frame);

                    frame.setContentPane(profilePanel);
                    frame.setVisible(true);
                });
            }else{
                MainPanel mainPanel = new MainPanel(frame);
                frame.setContentPane(mainPanel);
                frame.setVisible(true);
            }
        });
    }
}
