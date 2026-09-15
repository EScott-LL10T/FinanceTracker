package com.financetracker;
import javax.swing.*;
import java.awt.*;

public class EditProfilePanel extends JPanel {
    private final JFrame frame;
    private final Profile profile;

    private JTextField nameField;
    private JComboBox<String> roleComboBox;
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

        addTransactionButton.addActionListener(e -> editProfile());
    }

    private void editProfile(){
        MainPanel mainPanel = new MainPanel(frame);
        frame.setContentPane(mainPanel);
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

        form.add(new JLabel("name: "), gbc);

        nameField = new JTextField(profile.getName());
        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        form.add(new JLabel("Status:"), gbc);

        if(profile.getRole().equals("Employed")){
            roleComboBox = new JComboBox<>(
                    new String[]{"Employed", "Student"}
            );
        }else {
            roleComboBox = new JComboBox<>(
                    new String[]{"Student", "Employed"}
            );
        }

        gbc.gridx = 1;

        form.add(roleComboBox, gbc);


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

        return form;

    }

}
