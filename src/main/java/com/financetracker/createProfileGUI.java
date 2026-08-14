package com.financetracker;

import javax.swing.*;

public class createProfileGUI extends JFrame {
    private JTextField nameField;
    private JComboBox<String> roleComboBox;
    private JTextField debtField;
    private JTextField salaryField;
    private JButton submitButton;


    public createProfileGUI(){
        setTitle("Create Profile");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);




    }
}
