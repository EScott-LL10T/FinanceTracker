package com.financetracker;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class MainPanel extends JPanel {

    private final Profile profile;

    public MainPanel(JFrame frame) {
        // get data from database.
        profile = DatabaseHelper.getProfile();
        if(profile == null){
            return;
        }

        // Main layout
        setLayout(new BorderLayout());

        // Header Panel
        JLabel headerLabel = new JLabel(profile.getName() + " Budget Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 30));
        add(headerLabel, BorderLayout.NORTH);


        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                getPieChartPanel(),
                getTransactionsPanel()
        );

        splitPane.setDividerLocation(550);
        splitPane.setDividerSize(1);
        splitPane.setEnabled(false);

        splitPane.setOneTouchExpandable(false);

        splitPane.setResizeWeight(0.7);

        add(splitPane, BorderLayout.CENTER);

        Font buttonFont = new Font("Ariel", Font.PLAIN, 16);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(buttonFont);

        JButton addTransactionButton = new JButton("Add Transaction.");
        addTransactionButton.setFont(buttonFont);

        JButton editProfileButton = new JButton("Edit Profile");
        editProfileButton.setFont(buttonFont);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 20));

        buttonPanel.add(exitButton);
        buttonPanel.add(addTransactionButton);
        buttonPanel.add(editProfileButton);

        add(buttonPanel, BorderLayout.SOUTH);

        exitButton.addActionListener(e -> frame.dispose());

        addTransactionButton.addActionListener(e ->
                setNewFrameContent(frame, new AddTransactionPanel(frame, this))
        );

        editProfileButton.addActionListener(e ->
            setNewFrameContent(frame, new EditProfilePanel(frame, this))
        );

    }

    private JPanel getPieChartPanel(){
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        double salary = (profile.getSalary() - (profile.getSalary() * 0.40)) / 12.0;
        ArrayList<Transaction> transactions = DatabaseHelper.getTransactions();
        double totalSpent = 0.0;
        for (Transaction transaction : transactions) {
            double amount = transaction.getAmount();
            String category = transaction.getCategory();

            if (!dataset.getKeys().contains(category)) {
                dataset.setValue(category, amount);
            } else {
                double currentAmount = dataset.getValue(category).doubleValue();
                dataset.setValue(category, currentAmount + amount);
            }
            totalSpent += amount;
        }
        double available = salary - totalSpent;

        dataset.setValue("Available", available);

        JFreeChart pieChart = getPieChart(dataset);


        return new ChartPanel(pieChart);

    }

    private static JFreeChart getPieChart(DefaultPieDataset<String> dataset) {
        PiePlot<String> plot = new PiePlot<>(dataset);


        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator("{0}: £{1} ({2})");

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(labelGenerator);

        plot.setSectionPaint("Entertainment", Color.RED);
        plot.setSectionPaint("Rent", Color.BLUE);
        plot.setSectionPaint("Food", Color.GREEN);
        plot.setSectionPaint("Transport", Color.ORANGE);
        plot.setSectionPaint("Shopping", Color.YELLOW);
        plot.setSectionPaint("Other", Color.GRAY);
        plot.setSectionPaint("Available", new Color(128, 0, 128));

        plot.setLabelFont(new Font("Ariel", Font.PLAIN, 12));


        return new JFreeChart("Budget Distribution", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }


    private JPanel getTransactionsPanel(){
        JPanel transactionsPanel = new JPanel();
        Font transactionsFont = new Font("Arial", Font.PLAIN, 16);

        double monthlySalary = (profile.getSalary() - (profile.getSalary() * 0.40)) / 12;


        transactionsPanel.setLayout(new BoxLayout(transactionsPanel, BoxLayout.Y_AXIS));

        TitledBorder titledBorder = BorderFactory.createTitledBorder("Financial Summary");
        titledBorder.setTitleFont(new Font("Ariel", Font.BOLD, 20));

        transactionsPanel.setBorder(titledBorder);

        JLabel salary = new JLabel("Salary");
        salary.setFont(transactionsFont);

        String monthlySalaryFormatted = String.format("£%.2f", monthlySalary);

        JLabel salaryAmount = new JLabel(monthlySalaryFormatted);
        salaryAmount.setFont(transactionsFont);

        JLabel totalSpent = new JLabel("Total Spent");
        totalSpent.setFont(transactionsFont);

        double totalSpend = 100 + 250 + 300 + 800;
        JLabel totalSpentAmount = new JLabel(String.format("£%.2f", totalSpend));
        totalSpentAmount.setFont(transactionsFont);

        JLabel available = new JLabel("Available");
        available.setFont(transactionsFont);

        JLabel availableAmount = new JLabel(String.format("£%.2f", monthlySalary - 100 - 150 - 300 - 800));
        availableAmount.setFont(transactionsFont);

        JLabel taxed = new JLabel("tax");
        taxed.setFont(transactionsFont);

        JLabel taxedAmount = new JLabel(String.format("£%.2f", profile.getSalary() * 0.40 / 12));
        taxedAmount.setFont(transactionsFont);

        transactionsPanel.add(salary);
        transactionsPanel.add(salaryAmount);

        transactionsPanel.add(Box.createVerticalStrut(5));

        transactionsPanel.add(totalSpent);
        transactionsPanel.add(totalSpentAmount);

        transactionsPanel.add(Box.createVerticalStrut(5));

        transactionsPanel.add(available);
        transactionsPanel.add(availableAmount);

        transactionsPanel.add(Box.createVerticalStrut(5));

        transactionsPanel.add(taxed);
        transactionsPanel.add(taxedAmount);
        return transactionsPanel;

    }

    private void setNewFrameContent(JFrame frame, JPanel panel){
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }
}
