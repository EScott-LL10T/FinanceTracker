package com.financetracker;



import com.financetracker.errors.IllegalSQLMethodException;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTransactionPanel extends JPanel {
    private final JFrame frame;

    private JSpinner amountSpinner;
    private JComboBox<String> categoryComboBox;
    private JTextField descriptionTextField;
    private JSpinner dateSpinner;
    private JComboBox<String> typeComboBox;
    private Profile profile;
    private JLabel costLabel;
    private JLabel descriptionLabel;
    private JLabel typeLabel;

    public AddTransactionPanel(JFrame frame, MainPanel mainPanel){
        this.frame = frame;
        try {
            this.profile = DatabaseHelper.getProfile();
        } catch (IllegalSQLMethodException e) {
            e.printMessage();
            return;
        }

        setLayout(new BorderLayout());

        Font buttonFont = new Font("Arial", Font.PLAIN, 16);

        Font titleFont = new Font("SansSerif", Font.BOLD, 30);


        assert profile != null;
        JLabel title = new JLabel("Add Transaction " + profile.getName());
        title.setFont(titleFont);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        add(BorderLayout.NORTH, title);

        add(getForm(), BorderLayout.CENTER);


        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(buttonFont);

        JButton addTransactionButton = new JButton("Add Transaction");
        addTransactionButton.setFont(buttonFont);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 80, 80));
        buttonPanel.add(cancelButton);
        buttonPanel.add(addTransactionButton);

        categoryComboBox.addActionListener(e -> {
            if(categoryComboBox.getSelectedItem()!= null
                    && categoryComboBox.getSelectedItem().equals("Student Loan Payment")){
                costLabel.setText("Loan Payment Amount £:");
                descriptionLabel.setVisible(false);
                descriptionTextField.setVisible(false);
            }else{
                costLabel.setText("Cost £:");
                descriptionLabel.setVisible(true);
                descriptionTextField.setVisible(true);
            }

            if(categoryComboBox.getSelectedItem() != null && categoryComboBox.getSelectedItem().equals("Other")){
                typeLabel.setVisible(true);
                typeComboBox.setVisible(true);
            }else{
                typeLabel.setVisible(false);
                typeComboBox.setVisible(false);
            }

        });

        add(buttonPanel, BorderLayout.SOUTH);


        cancelButton.addActionListener(e -> {
            frame.setContentPane(mainPanel);
            frame.revalidate();
            frame.repaint();
        });

        addTransactionButton.addActionListener(e -> addTransaction());

    }

    private void addTransaction(){
        BigDecimal amount = Money.of((double) amountSpinner.getValue());
        String categoryText = (String) categoryComboBox.getSelectedItem();
        if(categoryText == null){
            categoryText = "Entertainment";
        }
        // "Expense", "Income"
        String transactionType = "Expense";
        if (categoryText.equals("Other")) {
            if(typeComboBox.getSelectedItem() != null){
                transactionType = typeComboBox.getSelectedItem().toString();
            }
        }else if(categoryComboBox.getSelectedItem().equals("Student Loan Payment")){
            transactionType = "Income";
        }
        String description = descriptionTextField.getText();
        if((description.isEmpty() || description.length() > 1000)
                && !categoryComboBox.getSelectedItem().equals("Student Loan Payment")){
            return;
        }
        Date date = (Date) dateSpinner.getValue();
        if(date == null && !categoryComboBox.getSelectedItem().equals("Student Loan Payment")) {
            return;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        String dateString = formatter.format(date);

        try {
            DatabaseHelper.addTransaction(amount, categoryText, description, dateString, profile.getId(),
                    transactionType);
        } catch (IllegalSQLMethodException e) {
            e.printMessage();
            return;
        }


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

        Font formFont = new Font("Arial", Font.PLAIN, 14);

        costLabel = new JLabel("Cost £:");
        costLabel.setFont(formFont);

        form.add(costLabel, gbc);

        amountSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000000.0,
                10.0));
        gbc.gridx = 1;
        gbc.weightx = 1;
        amountSpinner.setFont(formFont);

        form.add(amountSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(formFont);

        form.add(categoryLabel, gbc);

        if(profile.getRole().equals("Student")){
            categoryComboBox = new JComboBox<>(new String[]{"Entertainment", "Rent", "Food", "Transport", "Shopping",
                    "Student Loan Payment", "Other"});
        }else{
            categoryComboBox = new JComboBox<>(new String[]{"Entertainment", "Rent", "Food", "Transport", "Shopping",
                    "Other"});
        }

        gbc.gridx = 1;

        categoryComboBox.setFont(formFont);

        form.add(categoryComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        typeLabel = new JLabel("Type:");
        typeLabel.setFont(formFont);


        form.add(typeLabel, gbc);

        typeComboBox = new JComboBox<>(new String[]{"Expense", "Income"});
        typeComboBox.setFont(formFont);

        gbc.gridx = 1;

        form.add(typeComboBox, gbc);

        typeLabel.setVisible(false);
        typeComboBox.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy++;

        descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(formFont);

        form.add(descriptionLabel, gbc);

        descriptionTextField = new JTextField();

        gbc.gridx = 1;

        descriptionTextField.setFont(formFont);

        form.add(descriptionTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        JLabel dateLabel = new JLabel("Date of transaction:");
        dateLabel.setFont(formFont);

        form.add(dateLabel, gbc);

        SpinnerDateModel dateModel = new SpinnerDateModel();

        dateSpinner = new JSpinner(dateModel);

        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");

        dateSpinner.setEditor(dateEditor);

        gbc.gridx = 1;

        dateSpinner.setFont(formFont);

        form.add(dateSpinner, gbc);
        return form;
    }
}
