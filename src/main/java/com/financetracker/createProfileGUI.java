package com.financetracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class createProfileGUI extends JFrame {
    private final JTextField nameField;
    private final JComboBox<String> roleComboBox;
    private final JSpinner debtSpinner;
    private final JSpinner salarySpinner;


    public createProfileGUI(){
        setTitle("Create Profile");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JPanel header = getHeader();

        mainPanel.add(header, BorderLayout.NORTH);


        JPanel form = new JPanel(new GridBagLayout());

        form.setBorder(
                BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(25, 30, 25, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        form.add(new JLabel("Name:"), gbc);

        nameField = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(nameField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Status:"), gbc);

        roleComboBox = new JComboBox<>(
                new String[]{"Employed", "Student"}
        );

        gbc.gridx = 1;

        form.add(roleComboBox, gbc);

        // Debt
        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Debt:"), gbc);

        debtSpinner = new JSpinner(
                new SpinnerNumberModel(
                        0.0,
                        0.0,
                        10000000.0,
                        1000.0
                )
        );

        gbc.gridx = 1;

        form.add(debtSpinner, gbc);

        // Salary
        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Annual Salary:"), gbc);

        salarySpinner = new JSpinner(
                new SpinnerNumberModel(
                        0.0,
                        0.0,
                        10000000.0,
                        1000.0
                )
        );

        gbc.gridx = 1;

        form.add(salarySpinner, gbc);

        mainPanel.add(form, BorderLayout.CENTER);

        // Button
        JButton continueButton = new JButton("Continue");
        continueButton.setPreferredSize(new Dimension(150, 40));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(continueButton);


        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);


        continueButton.addActionListener(e -> continue_());

    }

    private JPanel getHeader() {
        Font titleFont = new Font("SansSerif", Font.BOLD, 26);


        JLabel title = new JLabel("Welcome to Finance Tracker");
        title.setFont(titleFont);
        title.setHorizontalAlignment(SwingConstants.CENTER);


        JLabel formTitle = new JLabel("Create a New Profile");
        formTitle.setFont(titleFont);
        formTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.add(title);
        header.add(formTitle);
        return header;
    }

    private void continue_(){
        String name = nameField.getText().trim();
        if(name.isEmpty() || name.length() > 2253){ // longest name currently is 2253.
            return;
        }
        Object objRole = roleComboBox.getSelectedItem();
        String role;
        if(objRole == null){
            role = "employed";
        }else{
            role = objRole.toString();
        }
        double debt = (double) debtSpinner.getValue();
        double salary = (double) salarySpinner.getValue();

        MainGUI mainGUI = new MainGUI();
        this.setVisible(false);
        mainGUI.setVisible(true);
    }

}
