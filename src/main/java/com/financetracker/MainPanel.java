package com.financetracker;

import javax.swing.*;
import java.awt.*;
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
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(headerLabel, BorderLayout.NORTH);


        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                getPieChartPanel(),
                getTransactionsPanel()
        );

        splitPane.setDividerLocation(450);
        splitPane.setDividerSize(1);
        splitPane.setEnabled(false);

        splitPane.setOneTouchExpandable(false);

        splitPane.setResizeWeight(0.7);

        add(splitPane, BorderLayout.CENTER);

        Font buttonFont = new Font("Ariel", Font.PLAIN, 14);

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

        addTransactionButton.addActionListener(e -> {
            AddTransactionGUI addTransactionGUI = new AddTransactionGUI(frame, this);
            frame.setContentPane(addTransactionGUI);
            frame.revalidate();
            frame.repaint();
        });

        editProfileButton.addActionListener(e -> {
            System.out.println("edit profile pressed");
        });



    }

    public JPanel getPieChartPanel(){
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        double salary = (profile.getSalary() - (profile.getSalary() * 0.40)) / 12;

        dataset.setValue("Rent", 800);
        dataset.setValue("Food", 300);
        dataset.setValue("Transport", 150);
        dataset.setValue("Entertainment", 100);
        dataset.setValue("Available", salary - 100 - 150 - 300 - 800);

        PiePlot<String> plot = new PiePlot<>(dataset);

        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator("{0}: £{1} ({2})");

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(labelGenerator);


        JFreeChart pieChart = new JFreeChart(
                "Budget Distribution",
                JFreeChart.DEFAULT_TITLE_FONT,
                plot,
                true
        );

        return new ChartPanel(pieChart);

    }


    public JPanel getTransactionsPanel(){
        JPanel transactionsPanel = new JPanel();
        Font font = new Font("Arial", Font.PLAIN, 16);

        double monthlySalary = (profile.getSalary() - (profile.getSalary() * 0.40)) / 12;


        transactionsPanel.setLayout(new BoxLayout(transactionsPanel, BoxLayout.Y_AXIS));

        transactionsPanel.setBorder(BorderFactory.createTitledBorder("Financial Summary"));

        JLabel salary = new JLabel("Salary");
        salary.setFont(font);

        String monthlySalaryFormatted = String.format("£%.2f", monthlySalary);

        JLabel salaryAmount = new JLabel(monthlySalaryFormatted);
        salaryAmount.setFont(font);

        JLabel totalSpent = new JLabel("Total Spent");
        totalSpent.setFont(font);

        double totalSpend = 100 + 250 + 300 + 800;
        JLabel totalSpentAmount = new JLabel(String.format("£%.2f", totalSpend));
        totalSpentAmount.setFont(font);

        JLabel available = new JLabel("Available");
        available.setFont(font);

        JLabel availableAmount = new JLabel(String.format("£%.2f", monthlySalary - 100 - 150 - 300 - 800));
        availableAmount.setFont(font);

        JLabel taxed = new JLabel("tax");
        taxed.setFont(font);

        JLabel taxedAmount = new JLabel(String.format("£%.2f", profile.getSalary() * 0.40 / 12));
        taxedAmount.setFont(font);

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
}
