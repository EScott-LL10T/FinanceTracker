package com.financetracker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class AddTransactionGUI extends JPanel {
    private final JFrame frame;
    private final JSpinner amountSpinner;
    private final JComboBox<String> categoryComboBox;
    private final JTextField descriptionTextField;
    private final JSpinner dateSpinner;

    public AddTransactionGUI(JFrame frame, MainPanel mainPanel){
        this.frame = frame;
        Profile profile = DatabaseHelper.getProfile();

        setLayout(new BorderLayout());

        Font buttonFont = new Font("Ariel", Font.PLAIN, 14);

        Font titleFont = new Font("SansSerif", Font.BOLD, 26);



        JLabel title = new JLabel("Add Transaction " + profile.getName());
        title.setFont(titleFont);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        add(BorderLayout.NORTH, title);



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
                10.0));
        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(amountSpinner, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Category:"), gbc);

        categoryComboBox = new JComboBox<>(new String[]{"Entertainment", "Rent", "Food", "Transport", "Shopping", "Clothes",
                "Other"});

        gbc.gridx = 1;

        form.add(categoryComboBox, gbc);

        // Debt
        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Description:"), gbc);

        descriptionTextField = new JTextField();

        gbc.gridx = 1;

        form.add(descriptionTextField, gbc);

        // Salary
        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Date of transaction:"), gbc);

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

        addTransactionButton.addActionListener(e -> addTransaction());

    }

    private void addTransaction(){
        int amount = (int) amountSpinner.getValue();
        String categoryText = (String) categoryComboBox.getSelectedItem();
        if(categoryText == null){
            return;
        }
        String description = descriptionTextField.getText();
        if(description.isEmpty() || description.length() > 1000){
            return;
        }
        LocalDateTime date = (LocalDateTime) dateSpinner.getValue();
        if(date == null){
            return;
        }
        String dateString = date.toString();
        DatabaseHelper.addTransaction(amount, categoryText, description, dateString);

        MainPanel mainPanel = new MainPanel(frame);
        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();

    }
}
