package com.financetracker;

import javax.swing.*;
import java.awt.*;

public class CreateProfilePanel extends JPanel{
    private final JFrame frame;
    private JTextField nameField;
    private JComboBox<String> roleComboBox;
    private JSpinner debtSpinner;
    private JSpinner salarySpinner;


    public CreateProfilePanel(JFrame frame){
        this.frame = frame;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JPanel header = getHeader();

        add(header, BorderLayout.NORTH);

        add(getForm(), BorderLayout.CENTER);

        // Button
        JButton continueButton = new JButton("Continue");
        continueButton.setPreferredSize(new Dimension(150, 40));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(continueButton);


        add(buttonPanel, BorderLayout.SOUTH);

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

        DatabaseHelper.createNewUser(name, role, debt, salary);

        frame.setContentPane(new MainPanel(frame));
        frame.revalidate();
        frame.repaint();
    }

    private JPanel getForm(){
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

        Font formFont = new Font("Ariel", Font.PLAIN, 14);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(formFont);

        form.add(nameLabel, gbc);

        nameField = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1;

        nameField.setFont(formFont);

        form.add(nameField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel roleLabel = new JLabel("Status:");
        roleLabel.setFont(formFont);

        form.add(roleLabel, gbc);

        roleComboBox = new JComboBox<>(
                new String[]{"Employed", "Student"}
        );

        gbc.gridx = 1;

        roleComboBox.setFont(formFont);

        form.add(roleComboBox, gbc);

        // Debt
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel debtLabel = new JLabel("Debt:");
        debtLabel.setFont(formFont);

        form.add(debtLabel, gbc);

        debtSpinner = new JSpinner(
                new SpinnerNumberModel(
                        0.0,
                        0.0,
                        10000000.0,
                        1000.0
                )
        );

        gbc.gridx = 1;

        debtSpinner.setFont(formFont);

        form.add(debtSpinner, gbc);

        // Salary
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel salaryLabel = new JLabel("Annual Salary:");

        salaryLabel.setFont(formFont);

        form.add(salaryLabel, gbc);

        salarySpinner = new JSpinner(
                new SpinnerNumberModel(
                        0.0,
                        0.0,
                        10000000.0,
                        1000.0
                )
        );

        salarySpinner.setFont(formFont);

        gbc.gridx = 1;

        form.add(salarySpinner, gbc);
        return form;
    }

}
