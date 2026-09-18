package com.financetracker;
import javax.swing.*;
import java.awt.*;

public class EditProfilePanel extends JPanel {
    private final JFrame frame;
    private final Profile profile;

    private JCheckBox deleteTransactionsCheckBox;
    private JTextField nameField;
    private JComboBox<String> newRoleComboBox;
    private JSpinner debtSpinner;
    private JSpinner salarySpinner;

    public EditProfilePanel(JFrame frame, MainPanel mainPanel){
        this.frame = frame;
        this.profile = DatabaseHelper.getProfile();

        setLayout(new BorderLayout());

        Font buttonFont = new Font("Ariel", Font.PLAIN, 14);

        Font titleFont = new Font("SansSerif", Font.BOLD, 26);

        assert profile != null;
        JLabel title = new JLabel("Edit Profile " + profile.getName());
        title.setFont(titleFont);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        add(BorderLayout.NORTH, title);

        add(BorderLayout.CENTER, getForm());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(buttonFont);

        JButton addTransactionButton = new JButton("Edit Profile");
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

        addTransactionButton.addActionListener(e -> {
            String message = "Are you sure you want to update your profile?";
            boolean deleteTransactions = deleteTransactionsCheckBox.isSelected();
            if(deleteTransactions){
                message = message + " All your transaction history will be deleted.";
            }
            int result = JOptionPane.showConfirmDialog(
                    frame,
                    message,
                    "Confirm Profile Update",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {

                editProfile();

                if (deleteTransactions) {
                    DatabaseHelper.deleteTransactions();
                }
            }
        });
    }

    private void editProfile(){
        String name = nameField.getText().trim();
        if(name.isEmpty() || name.length() > 2253){ // longest name currently is 2253.
            return;
        }
        Object objRole = newRoleComboBox.getSelectedItem();
        String role;
        if(objRole == null){
            role = profile.getRole();
        }else{
            role = objRole.toString();
        }
        double debt = (double) debtSpinner.getValue();
        double salary = (double) salarySpinner.getValue();

        DatabaseHelper.updateProfile(name, role, debt, salary);


        frame.setContentPane(new MainPanel(frame));
        frame.revalidate();
        frame.repaint();
    }

    private JPanel getForm(){
        JPanel form = new JPanel(new GridBagLayout());

        form.setBorder(
                BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(25, 30, 25, 30))
        );

        Font labelFont = new Font("Ariel", Font.PLAIN, 20);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);

        form.add(nameLabel, gbc);

        nameField = new JTextField(profile.getName());
        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Status:"), gbc);

        if(profile.getRole().equals("Employed")){
            newRoleComboBox = new JComboBox<>(
                    new String[]{"Employed", "Student"}
            );
        }else {
            newRoleComboBox = new JComboBox<>(
                    new String[]{"Student", "Employed"}
            );
        }

        gbc.gridx = 1;

        form.add(newRoleComboBox, gbc);


        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Debt:"), gbc);

        debtSpinner = new JSpinner(
                new SpinnerNumberModel(
                        profile.getDebt(),
                        0.0,
                        10000000.0,
                        1000.0
                )
        );

        gbc.gridx = 1;

        form.add(debtSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Annual Salary:"), gbc);

        salarySpinner = new JSpinner(
                new SpinnerNumberModel(
                        profile.getSalary(),
                        0.0,
                        10000000.0,
                        1000.0
                )
        );

        gbc.gridx = 1;

        form.add(salarySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Transactions:"), gbc);

        deleteTransactionsCheckBox = new JCheckBox("Delete all transactions");

        gbc.gridx = 1;

        form.add(deleteTransactionsCheckBox, gbc);

        return form;

    }

}
