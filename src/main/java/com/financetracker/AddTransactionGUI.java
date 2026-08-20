package com.financetracker;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;

public class AddTransactionGUI extends JPanel {
    private final JFrame frame;
    private final MainPanel mainPanel;
    private final JSpinner amountSpinner;
    private final JComboBox<String> category;
    private final JTextField description;
    private final JSpinner dateSpinner;
    private final Profile profile;

    public AddTransactionGUI(JFrame frame, MainPanel mainPanel){
        this.frame = frame;
        this.mainPanel = mainPanel;
        profile = DatabaseHelper.getProfile();

        setLayout(new BorderLayout());

        Font buttonFont = new Font("Ariel", Font.PLAIN, 14);



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

        form.add(new JLabel("Cost £:"), gbc);

        amountSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000000.0,
                1000.0));
        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(amountSpinner, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Category:"), gbc);

        category = new JComboBox<>(new String[]{"Entertainment", "Rent", "Food", "Transport", "Shopping", "Clothes",
                "Other"});

        gbc.gridx = 1;

        form.add(category, gbc);

        // Debt
        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Description:"), gbc);

        description = new JTextField();

        gbc.gridx = 1;

        form.add(description, gbc);

        // Salary
        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Annual Salary:"), gbc);

        SpinnerDateModel dateModel = new SpinnerDateModel();

        dateSpinner = new JSpinner(dateModel);

        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");

        dateSpinner.setEditor(dateEditor);

        gbc.gridx = 1;

        form.add(dateSpinner, gbc);

        add(form, BorderLayout.CENTER);














        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(buttonFont);

        JButton addTransactionButton = new JButton("Add Transaction");
        addTransactionButton.setFont(buttonFont);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 80, 80));
        buttonPanel.add(cancelButton);
        buttonPanel.add(addTransactionButton);

        add(buttonPanel, BorderLayout.SOUTH);

        cancelButton.addActionListener(e -> {
            frame.setContentPane(mainPanel);
            frame.revalidate();
            frame.repaint();
        });

    }
}
