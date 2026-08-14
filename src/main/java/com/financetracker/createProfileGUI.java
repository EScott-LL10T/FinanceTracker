package com.financetracker;

import javax.swing.*;
import java.awt.*;

public class createProfileGUI extends JFrame {
    private JTextField nameField;
    private JComboBox<String> roleComboBox;
    private JSpinner debtField;
    private JSpinner salaryField;
    private JButton submitButton;


    public createProfileGUI(){
        setTitle("Create Profile");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(5);

        JLabel roleLabel = new JLabel("Role:");
        roleComboBox = new JComboBox<>(
                new String[]{"Student", "Employed"}
        );

        JLabel debtLabel = new JLabel("Debt:");
        debtField = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000000, 1000));

        JLabel salaryLabel = new JLabel("Salary:");
        salaryField = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000000, 1000));

        submitButton = new JButton("Continue");

        JPanel panel = new JPanel(new GridLayout(5, 2, 0, 5));

        panel.add(nameLabel);
        panel.add(nameField);

        panel.add(roleLabel);
        panel.add(roleComboBox);

        panel.add(debtLabel);
        panel.add(debtField);

        panel.add(salaryLabel);
        panel.add(salaryField);

        panel.add(new JLabel());
        panel.add(submitButton);

        add(panel);

        setVisible(true);



    }

}
